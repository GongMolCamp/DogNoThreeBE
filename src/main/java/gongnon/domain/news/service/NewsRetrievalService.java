package gongnon.domain.news.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.dto.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

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
	private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 초기화

	public NewsResponseDto getTodayEconomyNews(String query) {
		String url = baseUrl + "?query=" + query + "&display=10&sort=date";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		headers.add("Accept", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			// API 응답 로그
			System.out.println("API 응답: " + response.getBody());

			JsonNode rootNode = objectMapper.readTree(response.getBody());
			JsonNode itemsNode = rootNode.path("items");

			if (!itemsNode.isArray() || itemsNode.isEmpty()) {
				// 검색 결과가 없는 경우 빈 응답 반환
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
