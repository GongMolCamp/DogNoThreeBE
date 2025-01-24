package gongnon.domain.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NewsRetrievalService {

    @Value("${naver-news.api.base-url}")
    private String baseUrl;  // 예: https://openapi.naver.com/v1/search/news.json
    @Value("${naver-news.api.client-id}")
    private String clientId;
    @Value("${naver-news.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NewsRepository newsRepository;

    /**
     * 예) "경제" 키워드 → 오늘의 경제 뉴스 *최대 5개*를 스크랩
     *    만약 일부 기사가 403 등으로 크롤링 실패하면,
     *    재시도(추가 페이지 호출)하여 *성공 기사*를 5개 맞출 때까지 반복 시도
     */
    @Transactional
    public NewsResponseDto getTodayEconomyNews(String query) {
        // 원하는 기사 개수
        int neededCount = 5;
        // 한 번에 가져올 개수 (페이지 크기)
        int display = 5;

        // 크롤링 재시도 로직으로 기사 확보
        return fetchAndScrapArticlesUntilSuccess(query, neededCount, display);
    }

    /**
     * 1) 처음에 Naver News API로 &display=5, &start=1 로 기사 목록
     * 2) Jsoup 크롤링 시도 → 성공 기사만 savedArticles에 담음
     * 3) 성공 기사 수 < neededCount 이면, start += display 로 다음 페이지 반복 호출
     * 4) 기사가 더 이상 없거나 neededCount개를 달성하면 중단
     */
    private NewsResponseDto fetchAndScrapArticlesUntilSuccess(String query, int neededCount, int display) {
        List<NewsArticleDto> savedArticles = new ArrayList<>();
        int start = 1;  // Naver API는 1부터 시작

        while (savedArticles.size() < neededCount) {
            // 1) API 호출하여 기사 목록 받아오기
            List<NewsArticleDto> articles = fetchArticles(query, start, display);

            // 더 이상 기사가 없다면 중단
            if (articles.isEmpty()) {
                break;
            }

            // 2) 각 기사 크롤링 시도
            for (NewsArticleDto dto : articles) {
                try {
                    // Jsoup 크롤링 + DB 저장 시도
                    NewsArticle saved = scrapArticleAndSaveToDB(dto);
                    if (saved != null) {
                        // 스크랩 성공한 기사만 리스트에 담음
                        savedArticles.add(dto);
                    }
                } catch (Exception e) {
                    // 403 등으로 스크랩 실패 → 그냥 무시
                    System.out.println("크롤링 실패: " + e.getMessage());
                }

                // 만약 neededCount개가 되면 즉시 종료
                if (savedArticles.size() == neededCount) {
                    break;
                }
            }

            // 현재 페이지 기사들을 모두 시도했으니, 다음 페이지로 넘어감
            start += display;
        }

        // 최종적으로 스크랩 성공한 기사들을 담아서 반환
        return new NewsResponseDto(savedArticles);
    }

    /**
     * Naver News API 호출(검색) → 기사 items 만 파싱하여 List로 리턴
     */
    private List<NewsArticleDto> fetchArticles(String query, int start, int display) {
        String url = baseUrl
                + "?query=" + query
                + "&start=" + start
                + "&display=" + display
                + "&sort=sim";

        try {
            // 1) API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class
            );

            // 2) JSON 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return Collections.emptyList();
            }

            // 3) items 배열 -> List<NewsArticleDto> 변환
            List<NewsArticleDto> articles = objectMapper.convertValue(
                    itemsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class)
            );
            return articles;

        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Jsoup으로 기사 본문을 긁어서 DB에 저장
     */
    public NewsArticle scrapArticleAndSaveToDB(NewsArticleDto articleDto) {
        try {
            // (1) 기사 URL
            String articleUrl = convertToPcUrlIfMobile(articleDto.getLink());

            // (2) Jsoup 연결
            Connection connection = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0")
                    .referrer("https://news.naver.com")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .timeout(10_000)
                    .followRedirects(true)
                    .maxBodySize(0);

            // (3) HTML 파싱 및 본문 추출 (여러 Selector 시도)
            Document document = connection.get();
            String[] selectors = { "div#newsct_article", "article#dic_area", "div#dic_area", "div#contents" };
            Element articleBody = null;
            for (String sel : selectors) {
                articleBody = document.selectFirst(sel);
                if (articleBody != null) break;
            }

            // (4) 제목 정리
            String cleanTitle = sanitizeTitle(articleDto.getTitle());
            articleDto.setTitle(cleanTitle);

            // (5) 본문 만들기
            String fullContent = "본문을 가져올 수 없습니다.";
            if (articleBody != null) {
                // <p> 태그 기준으로 합치되, <br>은 개행으로 치환
                Elements paragraphs = articleBody.select("p");
                if (!paragraphs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Element p : paragraphs) {
                        String pHtml = p.html().replaceAll("<br ?/?>", "\n");
                        sb.append(Jsoup.parse(pHtml).text()).append("\n");
                    }
                    fullContent = sb.toString().trim();
                } else {
                    // <p> 태그가 없는 경우 전체 HTML에서 <br> 처리
                    String bodyHtml = articleBody.html().replaceAll("<br ?/?>", "\n");
                    fullContent = Jsoup.parse(bodyHtml).text().trim();
                }
            }

            // (6) 본문 DTO에 담음
            articleDto.setDescription(fullContent);

            // (7) pubDate -> LocalDateTime 변환
            LocalDateTime pubDate = parseDate(articleDto.getPubDate());

            // (8) DB 저장
            if (Objects.equals(fullContent, "본문을 가져올 수 없습니다.")) {
                return null;
            }
            NewsArticle newsArticle = new NewsArticle(
                    articleDto.getTitle(),
                    articleDto.getLink(),
                    articleDto.getDescription(),  // fullContent
                    pubDate
            );
            newsRepository.save(newsArticle);
            return newsArticle;
        } catch (Exception e) {
            throw new RuntimeException("크롤링/DB저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 제목 정리 (HTML 태그 제거 + HTML 엔티티 디코딩)
     */
    private String sanitizeTitle(String rawTitle) {
        if (rawTitle == null) return "";
        // (1) HTML 태그 제거
        String noHtml = Jsoup.parse(rawTitle).text();
        // (2) HTML 엔티티(&quot; 등) 디코딩
        return StringEscapeUtils.unescapeHtml4(noHtml);
    }

    /**
     * 모바일 URL -> PC URL 로 변환
     */
    private String convertToPcUrlIfMobile(String url) {
        if (url.contains("m.news.naver.com")) {
            url = url.replace("m.news.naver.com", "news.naver.com");
        }
        return url;
    }

    /**
     * pubDate(예: "Tue, 10 Jan 2025 12:34:56 +0900") -> LocalDateTime 파싱
     */
    private LocalDateTime parseDate(String pubDateStr) {
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            return LocalDateTime.parse(pubDateStr, formatter);
        } catch (Exception e) {
            // 파싱 실패시 현재 시각으로
            return LocalDateTime.now();
        }
    }

    /**
     * Naver News API 호출 시 필요한 헤더
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");
        return headers;
    }
}
