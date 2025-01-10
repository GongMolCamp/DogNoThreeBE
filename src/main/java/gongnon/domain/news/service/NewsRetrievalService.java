package gongnon.domain.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsRetrievalService {

    @Value("${naver-news.api.base-url}")
    private String baseUrl;               // 예: https://openapi.naver.com/v1/search/news.json
    @Value("${naver-news.api.client-id}")
    private String clientId;
    @Value("${naver-news.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NewsRepository newsRepository;

    /**
     * 오늘의 경제 뉴스 10개
     */
    @Transactional
    public NewsResponseDto getTodayEconomyNews(String query) {
        return fetchNews(query, 10);
    }

    /**
     * Naver News API로 기사 목록 가져오기 → Jsoup으로 본문 크롤링 후 DB 저장
     */
    private NewsResponseDto fetchNews(String query, int display) {
        String url = baseUrl + "?query=" + query + "&display=" + display + "&sort=sim";

        try {
            // 1) API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class
            );

            // 2) JSON 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return new NewsResponseDto(List.of());
            }

            // 3) items 배열 → NewsArticleDto 리스트 변환
            List<NewsArticleDto> articles = objectMapper.convertValue(
                itemsNode,
                objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class)
            );

            // 4) 각 기사 본문 크롤링 & DB 저장
            articles.forEach(this::scrapArticleAndSaveToDB);

            return new NewsResponseDto(articles);

        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Jsoup으로 기사 본문을 긁어서 DB에 저장
     */
    public NewsArticle scrapArticleAndSaveToDB(NewsArticleDto articleDto) {
        try {
            // (a) 기사 URL
            String articleUrl = convertToPcUrlIfMobile(articleDto.getLink());

            // (b) Jsoup 연결
            Connection connection = Jsoup.connect(articleUrl)
                .userAgent("Mozilla/5.0")
                .referrer("https://news.naver.com")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .timeout(10_000)
                .followRedirects(true)
                .maxBodySize(0);

            // (c) HTML 파싱
            Document document = connection.get();

            // (d) 본문 추출 (여러 Selector 시도)
            String[] selectors = { "div#newsct_article", "article#dic_area", "div#dic_area", "div#contents" };
            Element articleBody = null;
            for (String sel : selectors) {
                articleBody = document.selectFirst(sel);
                if (articleBody != null) break;
            }

            // (e) fullContent 만들기
            String fullContent = "본문을 가져올 수 없습니다.";
            if (articleBody != null) {
                // <p> 태그 기준으로 합치되, <br>은 개행으로 치환
                Elements paragraphs = articleBody.select("p");
                if (!paragraphs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Element p : paragraphs) {
                        // <br> 처리
                        String pHtml = p.html().replaceAll("<br ?/?>", "\n");
                        sb.append(Jsoup.parse(pHtml).text()).append("\n");
                    }
                    fullContent = sb.toString().trim();
                } else {
                    // <p> 태그가 없는 경우 전체에서 <br> 처리
                    String bodyHtml = articleBody.html().replaceAll("<br ?/?>", "\n");
                    fullContent = Jsoup.parse(bodyHtml).text().trim();
                }
            }

            // (f) 본문을 DTO에 담음
            articleDto.setDescription(fullContent);

            // (g) pubDate → LocalDateTime 변환
            LocalDateTime pubDate = parseDate(articleDto.getPubDate());

            // (h) DB 저장
            NewsArticle newsArticle = new NewsArticle(
                articleDto.getTitle(),          // 제목
                articleDto.getLink(),           // 링크
                articleDto.getDescription(),    // ★ fullContent를 description 에 저장
                pubDate
            );
            newsRepository.save(newsArticle);

            return newsArticle;

        } catch (Exception e) {
            throw new RuntimeException("크롤링/DB저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 모바일 URL을 PC URL로 간단 변환
     */
    private String convertToPcUrlIfMobile(String url) {
        if (url.contains("m.news.naver.com")) {
            url = url.replace("m.news.naver.com", "news.naver.com");
        }
        // 필요 시 추가 변환 로직
        return url;
    }

    /**
     * pubDate 형식 → LocalDateTime 파싱
     * (예: "Tue, 10 Jan 2025 12:34:56 +0900")
     */
    private LocalDateTime parseDate(String pubDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            return LocalDateTime.parse(pubDateStr, formatter);
        } catch (Exception e) {
            // 혹은 오늘 날짜로 세팅 등
            return LocalDateTime.now();
        }
    }

    /**
     * Naver News API 호출 시 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");
        return headers;
    }
}
