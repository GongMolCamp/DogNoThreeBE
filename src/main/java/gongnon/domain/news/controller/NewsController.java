package gongnon.domain.news.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.service.NewsRetrievalService;
import gongnon.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final NewsRetrievalService newsRetrievalService;
    private final NewsService newsService;
    // 오늘자 네이버 뉴스 불러오기 (10개)
    @GetMapping("/today")
    public NewsResponseDto getTodayEconomyNews(@RequestParam(defaultValue = "경제") String query) {
        return newsRetrievalService.getTodayEconomyNews(query);
    }

    // 특정 뉴스 기사를 ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleDto> getNewsById(@PathVariable Long id) {
        NewsArticleDto newsArticleDto = newsService.findDtoById(id); // DTO 반환 로직 호출
        if (newsArticleDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newsArticleDto);
    }
}
