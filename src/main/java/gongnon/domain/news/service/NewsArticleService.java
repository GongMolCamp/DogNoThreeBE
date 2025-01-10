package gongnon.domain.news.service;

import org.springframework.stereotype.Service;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsArticleService {
	private final NewsArticleRepository newsArticleRepository;

	public NewsArticleDto createArticle(String title, String description) {
		NewsArticle newsArticle = new NewsArticle(title, description);
		newsArticleRepository.save(newsArticle);
		return NewsArticleDto.builder()
			.title(newsArticle.getTitle())
			.description(newsArticle.getDescription())
			.build();
	}
}
