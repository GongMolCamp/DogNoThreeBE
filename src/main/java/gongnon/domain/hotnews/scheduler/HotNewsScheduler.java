package gongnon.domain.hotnews.scheduler;

import gongnon.domain.hotnews.service.HotNewsArticleService;
import gongnon.domain.hotnews.repository.PredefinedPressRepository;
import gongnon.domain.hotnews.model.PredefinedPress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.time.LocalDate;

@Component
public class HotNewsScheduler {
    @Autowired
    private HotNewsArticleService hotNewsArticleService;

    @Autowired
    private PredefinedPressRepository predefinedPressRepository;

    // 매일 자정(00:00)에 실행
    @Scheduled(cron = "0 0 15 * * *") // UTC 기준 15ㅣ -> 한국 기준 자정 00시
    public void updateDailyTop5News() {
        LocalDate today = LocalDate.now();
        // 전체 언론사 업데이트
        hotNewsArticleService.updateAllPressTop5News(today);
        System.out.println("Daily top 5 news has been updated successfully.");
    }
}
