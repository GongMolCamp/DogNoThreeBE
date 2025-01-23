package gongnon.domain.news.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.news.dto.NewsArticleDto;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.service.NewsRetrievalService;
import gongnon.domain.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "News", description = "뉴스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final NewsRetrievalService newsRetrievalService;
    private final NewsService newsService;
    @Operation(summary = "오늘자 뉴스 10개 불러오기")
    @GetMapping("/today")
    public NewsResponseDto getTodayEconomyNews(
        @Parameter(description = "키워드", required = true, example = "경제")
        @RequestParam(defaultValue = "경제") String query
    ) {
        return newsRetrievalService.getTodayEconomyNews(query);
    }

    @Operation(summary = "특정 뉴스 기사 조회")
    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleDto> getNewsById(
        @Parameter(description = "뉴스 기사 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        NewsArticleDto newsArticleDto = newsService.findDtoById(id); // DTO 반환 로직 호출
        if (newsArticleDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newsArticleDto);
    }
}
