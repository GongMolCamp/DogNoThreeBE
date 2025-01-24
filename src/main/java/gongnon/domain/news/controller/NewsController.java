package gongnon.domain.news.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.service.NewsRetrievalService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsRetrievalService newsRetrievalService;
    // 오늘자 네이버 경제 뉴스 불러오기 (10개)
    @GetMapping("/news/economy/today")
    public NewsResponseDto getTodayEconomyNews(@RequestParam(defaultValue = "경제") String query) {
        return newsRetrievalService.getTodayEconomyNews(query);
    }
/*
    @GetMapping("/news/today")
    public NewsResponseDto getTodayNews(@RequestParam String query) {
        return newsRetrievalService.getTodayNews(query);
    }*/
}
