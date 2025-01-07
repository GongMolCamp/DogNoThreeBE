package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.dto.NewsArticleDto;
import gongnon.domain.gpt.model.NewsArticle;
import gongnon.domain.gpt.service.NewsArticleService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    // (1) 기사 등록 + 요약 생성

    @PostMapping
    public NewsArticleDto createArticle(@RequestBody NewsArticleDto dto){
        NewsArticle article = newsArticleService.createAndSummarize(dto.getTitle(), dto.getContent());
        return new NewsArticleDto(article);
    }

    // (2) 기사 조회

    @GetMapping
    public NewsArticleDto getArticle(@PathVariable Long id){
        NewsArticle article = newsArticleService.getById(id);
        return new NewsArticleDto(article);
    }
}
