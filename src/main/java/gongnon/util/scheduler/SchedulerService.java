package gongnon.util.scheduler;

import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.service.NewsRetrievalService;
import gongnon.domain.user.model.User;
import gongnon.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class SchedulerService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NewsRetrievalService newsRetrievalService;

	@Scheduled(cron = "0 * * * * *") // 매 분마다 실행
	public void checkNotificationTime() {
		String currentTime = LocalTime.now().withSecond(0).toString().substring(0, 5); // "HH:mm" 형식으로 자르기

		List<User> users = userRepository.findByNotificationTime(currentTime);
		System.out.println("DB 조회 시간: " + currentTime + ", 조회된 사용자 수: " + users.size());

		if (!users.isEmpty()) {
			users.forEach(user -> {
				System.out.println("유저: " + user.getName() + ", 뉴스 취향: " + user.getNewsPreference());

				// 1. 유저의 뉴스 취향을 기반으로 뉴스 가져오기
				try {
					NewsResponseDto newsResponse = newsRetrievalService.getTodayEconomyNews(user.getNewsPreference());
					System.out.println("뉴스 기사 개수: " + newsResponse.getArticles().size());

					// 2. 가져온 뉴스의 기사 본문 크롤링 및 저장 후 결과 출력
					newsResponse.getArticles().forEach(article -> {
						System.out.println("제목: " + article.getTitle() + ", 링크: " + article.getLink());
						try {
							NewsArticle savedArticle = newsRetrievalService.scrapArticleAndSaveToDB(article);
							System.out.println("저장된 기사: " + savedArticle.getTitle());
						} catch (Exception e) {
							System.err.println("기사 본문 크롤링 실패: " + e.getMessage());
						}
					});

				} catch (Exception e) {
					System.err.println("뉴스 가져오기 실패: " + e.getMessage());
				}
			});
		} else {
			System.out.println("알림을 보낼 사용자가 없습니다.");
		}
	}
}
