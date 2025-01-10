package gongnon.util.scheduler;

import gongnon.domain.gpt.service.SummarizeService;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.service.NewsRetrievalService;
import gongnon.domain.sms.service.SmsMessagingService;
import gongnon.domain.sms.service.SmsService;
import gongnon.domain.user.model.User;
import gongnon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class SchedulerService {
	private final UserRepository userRepository;
	private final NewsRetrievalService newsRetrievalService;
	private final SummarizeService summarizeService;
	private final SmsService smsService;
	private final SmsMessagingService smsMessagingService;

	@Scheduled(cron = "0 * * * * *") // 매 분마다 실행
	public void checkNotificationTime() {
		String currentTime = LocalTime.now().withSecond(0).toString().substring(0, 5); // "HH:mm" 형식으로 자르기
		List<User> users = userRepository.findByNotificationTime(currentTime);

		if (!users.isEmpty()) {
			users.forEach(user -> {
				System.out.println("유저: " + user.getName() + ", 뉴스 취향: " + user.getNewsPreference());
				StringBuilder message = new StringBuilder();
				message.append(" 안녕하세요, " + user.getName() + "님! \n 오늘의 " + user.getNewsPreference() + " 뉴스 요약입니다.\n ");
				AtomicInteger i = new AtomicInteger(1);

				// 1. 유저의 뉴스 취향을 기반으로 뉴스 가져오기
				try {
					NewsResponseDto newsResponse = newsRetrievalService.getTodayEconomyNews(user.getNewsPreference());

					// 2. 가져온 뉴스의 기사 본문 크롤링 및 저장 후 결과 출력
					newsResponse.getArticles().forEach(article -> {
						NewsArticle savedArticle = newsRetrievalService.scrapArticleAndSaveToDB(article);

						// 3. 가져온 뉴스 기사 제목 및 본문 내용을 gpt api를 이용하여 요약하기
						if (savedArticle != null) {
							String summary = summarizeService.summarize(savedArticle.getId());
							message.append(i.getAndIncrement() + ". " + savedArticle.getTitle() + "\n" + summary + "\n\n");
						}
					});

					// 4. 요약된 내용을 사용자에게 SMS 문자 메시지로 보내기
					System.out.println(user.getName() + "님에게 보낼 메시지 : " + message.toString());
					smsService.sendSms(user.getPhone(), message.toString());
					smsMessagingService.saveMessage(user, message.toString());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			});
		} else {
			System.out.println("알림을 보낼 사용자가 없습니다.");
		}
	}
}
