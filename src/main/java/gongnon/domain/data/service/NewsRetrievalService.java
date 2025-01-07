// 패키지 선언
package gongnon.domain.data.service;
// package gongnon.domain.news.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
// import java.util.stream.Collectors;

// Spring 관련 import
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// DTO 관련 import (DTO = Data Transfer Object)
import gongnon.domain.data.dto.NewsArticleDto; // 뉴스 기사 표한하기 위한 DTO(뉴스 제목, 링크, 설명 등등 저장하는 클래스)
import gongnon.domain.data.dto.NewsResponseDto; // 여러 개의 뉴스 기사 감싸서 반환하기 위한 DTO
// Lombok 관련 import
import lombok.RequiredArgsConstructor;
// Spring 관련 import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

// JSON 처리 관련 import
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public NewsResponseDto getTodayEconomyNews(String query) {
        String url = baseUrl + "?query=" + query + "&display=10&sort=date";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // JSON 데이터를 디코딩하여 출력
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            System.out.println("API 응답: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return new NewsResponseDto(List.of());
            }

            List<NewsArticleDto> articles = objectMapper.convertValue(itemsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class));

            return new NewsResponseDto(articles);
        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출에 실패했습니다. 에러 메시지: " + e.getMessage());
        }
    }

    public NewsResponseDto getWeeklyEconomyNews(String query) {
        // 일주일치 뉴스를 받아야하니 넉넉하게 100개 받아오기
        String url = baseUrl + "?query=" + query + "&display=100&sort=date";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            System.out.println("API 응답: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return new NewsResponseDto(List.of());
            }

            List<NewsArticleDto> articles = objectMapper.convertValue(itemsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class));

            // 다른 두 클래스는 오늘의 날짜를 기준으로 받아오면 되지만 이 클래스는 일주일치를 받아야하므로 추가 작업 필요
            // 네이버 뉴스 api => 날짜 범위 필터링 지원 x
            LocalDateTime beforeOneWeekTime = LocalDateTime.now().minusDays(7); // 사용자의 현재 날짜를 받아 그 날짜에 7일을 뺀 날짜를 선언
            // 네이버 API의 pubDate 형식(Tue, 07, Jan 2025 10:00:00 +0900)에 맞추기
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

            List<NewsArticleDto> includedWeekArticles = articles.stream()
                    .filter(article -> {
                        try{
                            LocalDateTime pubDate = LocalDateTime.parse(article.getPubDate(), dateTimeFormatter);
                            return pubDate.isAfter(beforeOneWeekTime);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            return new NewsResponseDto(includedWeekArticles);
        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출에 실패했습니다. 에러 메시지: " + e.getMessage());
        }
    }

    public NewsResponseDto getTodaysHotNews(String query) {
        String url = baseUrl + "?query=" + query + "&display=5&sort=sim"; // sim이 관련도 순 즉 인기 순 5개

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.add("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // JSON 데이터를 디코딩하여 출력
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            System.out.println("API 응답: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

            JsonNode itemsNode = rootNode.path("items");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return new NewsResponseDto(List.of());
            }

            List<NewsArticleDto> articles = objectMapper.convertValue(itemsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticleDto.class));

            return new NewsResponseDto(articles);
        } catch (Exception e) {
            throw new RuntimeException("Naver News API 호출에 실패했습니다. 에러 메시지: " + e.getMessage());
        }
    }
}
