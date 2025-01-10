package gongnon.domain.news.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.service.NewsArticleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/news")
public class NewsArticleController {
	private final NewsArticleService newsArticleService;

	@PostMapping("/article")
	public NewsArticleDto createArticle(@RequestBody NewsArticleDto newsArticleDto) {
		return newsArticleService.createArticle(newsArticleDto.title(), newsArticleDto.description());
	}
}
