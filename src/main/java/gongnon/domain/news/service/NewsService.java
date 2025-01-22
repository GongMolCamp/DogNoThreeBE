package gongnon.domain.news.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {
	private final NewsRepository newsRepository;

	// ID로 NewsArticle을 찾아서 DTO로 변환
	@Transactional(readOnly = true)
	public NewsArticleDto findDtoById(Long id) {
		NewsArticle newsArticle = newsRepository.findById(id).orElse(null);
		if (newsArticle == null) {
			return null;
		}

		// NewsArticle -> NewsArticleDto 변환
		return new NewsArticleDto(
			newsArticle.getTitle(),
			newsArticle.getNewsLink(),
			newsArticle.getNewsLink(),
			newsArticle.getDescription(),
			newsArticle.getPublicatedDate().toString()
		);
	}
}
