package gongnon.domain.data.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.data.dto.NewsResponseDto;
import gongnon.domain.data.service.NewsRetrievalService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsRetrievalService newsRetrievalService;
    // 1. 오늘자 네이버 경제 뉴스 불러오기 (10개)
    @GetMapping("/news/economy/today")
    public NewsResponseDto getTodayEconomyNews(@RequestParam(defaultValue = "경제") String query) {
        return newsRetrievalService.getTodayEconomyNews(query);
    }

    // 2. 일주일치 네이버 경제 뉴스 불러오기 => 100개를 받아도 뉴스가 너무 많아서 하루치 밖에 안가져와짐. 해결 필요
    @GetMapping("/news/economy/week")
    public NewsResponseDto getWeeklyEconomyNews(@RequestParam(defaultValue = "경제") String query) {
        return newsRetrievalService.getWeeklyEconomyNews(query);
    }

    // 3. 오늘 가장 인기 있는 뉴스 5개 불러오기
    @GetMapping("/news/economy/TodaysHot")
    public NewsResponseDto getTodaysHotNews(@RequestParam(defaultValue = "경제") String query) {
        return newsRetrievalService.getTodaysHotNews(query);
    }
}
