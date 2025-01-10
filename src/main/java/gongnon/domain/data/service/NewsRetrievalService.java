// 패키지 선언
package gongnon.domain.data.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.ArrayList;

import gongnon.domain.data.dto.NewsArticleDto;
import gongnon.domain.data.dto.NewsResponseDto;
import gongnon.domain.data.model.NewsArticle;
import gongnon.domain.data.repository.NewsRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NewsRetrievalService {

    @Value("${naver-news.api.base-url}")
    private String baseUrl;

    @Value("${naver-news.api.client-id}")
    private String clientId;

    @Value("${naver-news.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NewsRepository newsRepository;

    @Transactional
    public NewsResponseDto getTodayEconomyNews(String query) {
        return fetchNews(query, 10, "sim", null);
    }

    @Transactional
    public NewsResponseDto getWeeklyEconomyNews(String query) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return fetchNews(query, 20, "sim", oneWeekAgo);
    }

    @Transactional
    public NewsResponseDto getTodaysHotNews(String query) {
        return fetchNews(query, 5, "sim", null);
    }

    private NewsResponseDto fetchNews(String query, int display, String sort, LocalDateTime filterDate) {
        String url = baseUrl + "?query=" + query + "&display=" + display + "&sort=" + sort;

        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return new NewsResponseDto(List.of());
            }

            List<NewsArticleDto> articles = objectMapper.convertValue(itemsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class));

            if (filterDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                articles = articles.stream()
                        .filter(article -> {
                            try {
                                LocalDateTime pubDate = LocalDateTime.parse(article.getPubDate(), formatter);
                                return pubDate.isAfter(filterDate);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
            }

            articles.forEach(this::scrapArticleAndSaveToDB);
            return new NewsResponseDto(articles);
        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출에 실패했습니다. 에러 메시지: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");
        return headers;
    }

    private void scrapArticleAndSaveToDB(NewsArticleDto articleDto) {
        try {
            String articleUrl = articleDto.getLink();
            Document naverNews = Jsoup.connect(articleUrl).get();
            Element articleBody = naverNews.selectFirst("#newsct_article");

            if (articleBody != null) {
                articleDto.setDescription(articleBody.text());
            } else {
                articleDto.setDescription("기사 본문을 가져오는데 실패하였습니다.");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime pubDate = LocalDateTime.parse(articleDto.getPubDate(), formatter);

            NewsArticle newsArticle = new NewsArticle(
                    articleDto.getTitle(),
                    articleDto.getLink(),
                    articleDto.getDescription(),
                    pubDate
            );

            newsRepository.save(newsArticle);
        } catch (Exception e) {
            articleDto.setDescription("크롤링 중 오류 발생: " + e.getMessage());
        }
    }
}
