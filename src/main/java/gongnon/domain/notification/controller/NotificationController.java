package gongnon.domain.notification.controller;

import gongnon.domain.gpt.service.SummarizeService;
import gongnon.domain.news.dto.NewsResponseDto;
import gongnon.domain.news.model.NewsArticle;
import gongnon.domain.news.service.NewsRetrievalService;
import gongnon.domain.notification.dto.NotificationDto;
import gongnon.domain.notification.dto.NotificationResponseDto;
import gongnon.domain.notification.dto.NotificationSendRequest;
import gongnon.domain.notification.dto.NotificationSummaryResponse;
import gongnon.domain.notification.model.NotificationRecord;
import gongnon.domain.notification.repository.NotificationRecordRepository;
import gongnon.domain.notification.service.AppNotificationService;
import gongnon.domain.user.model.User;
import gongnon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final UserRepository userRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final AppNotificationService appNotificationService;

    private final NewsRetrievalService newsRetrievalService;
    private final SummarizeService summarizeService;

    /**
     * 특정 사용자의 알림 기록 조회
     * ex) GET /notifications/user/1
     */
    @GetMapping("/user/{userId}")
    public List<NotificationResponseDto> getNotificationsByUser(@PathVariable Long userId) {
        // 1) 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found, id=" + userId));

        // 2) 해당 유저의 알림 기록 조회
        List<NotificationRecord> records = notificationRecordRepository.findByUser(user);

        // 3) 엔티티 -> DTO 변환
        return records.stream()
                .map(NotificationResponseDto::fromEntity)
                .toList();
    }

    /**
     * 특정 사용자에게 알림을 즉시 발송 (수동 트리거) + DB 저장
     * ex) POST /notifications/user/1
     * body: { "message": "이런저런 내용..." }
     */
    @PostMapping("/user/{userId}")
    public String sendNotification(@PathVariable Long userId,
                                   @RequestBody NotificationSendRequest request) {
        // 1) 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found, id=" + userId));

        // 2) 알림 전송
        appNotificationService.sendNotificationToApp(user, request.getMessage());

        // 3) DB 저장
        NotificationRecord record = appNotificationService.saveNotificationRecord(user, request.getMessage());

        return "Notification sent & saved. Record ID = " + record.getId();
    }


    // test - 스케줄러 안하고 postman 직접확인
    // http://localhost:8080/notifications/test?keyword=경제&userId=1
    @GetMapping("/test")
    public String testNaverGptFlow(
            @RequestParam String keyword,   // 네이버 뉴스 검색 키워드
            @RequestParam Long userId       // 알림을 보낼 사용자 ID
    ) {
        // 1) 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found, id=" + userId));

        // 2) 네이버 뉴스 API 호출 → 뉴스 목록 얻기
        NewsResponseDto newsResponse = newsRetrievalService.getTodayEconomyNews(keyword);

        // 3) 각 기사를 DB 저장 + GPT 요약 후, 메시지 구성
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("안녕하세요, ").append(user.getName()).append("님!\n")
                .append("키워드 [").append(keyword).append("] 뉴스 요약입니다.\n\n");

        AtomicInteger idx = new AtomicInteger(1);
        newsResponse.getArticles().forEach(articleDto -> {
            // a) 기사 DB 저장
            NewsArticle savedArticle = newsRetrievalService.scrapArticleAndSaveToDB(articleDto);

            if (savedArticle != null) {
                // b) GPT 요약
                String summary = summarizeService.summarize(savedArticle.getId());

                // c) 메시지에 붙이기
                messageBuilder
                        .append(idx.getAndIncrement()).append(". ")
                        .append(savedArticle.getTitle()).append("\n")
                        .append(summary).append("\n\n");
            }
        });

        String finalMessage = messageBuilder.toString();

        // 4) 앱 알림 전송
        appNotificationService.sendNotificationToApp(user, finalMessage);

        // 5) DB(notification_record)에 기록
        NotificationRecord record = appNotificationService.saveNotificationRecord(user, finalMessage);

        // 6) 결과 반환
        return String.format("알림 전송 + DB 기록 완료!\nRecord ID = %d\n메시지: %s",
                record.getId(),
                finalMessage);
    }


    @GetMapping("/user/{userId}/summary")
    public NotificationSummaryResponse getNotificationSummary(@PathVariable Long userId) {
        // 1) 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없음, id=" + userId));

        // 2) 가장 최근 알림 1개
        NotificationRecord latestRecord = notificationRecordRepository
                .findTopByUserOrderBySentAtDesc(user);

        // 3) 전체 알림 (내림차순)
        List<NotificationRecord> allRecords = notificationRecordRepository
                .findByUserOrderBySentAtDesc(user);


        // 4) DTO 변환
        NotificationDto latestDto = (latestRecord != null)
                ? NotificationDto.fromEntity(latestRecord)
                : null; // 없을 경우 null

        List<NotificationDto> allDtos = allRecords.stream()
                .map(NotificationDto::fromEntity)
                .toList();

        // 5) 응답 생성
        return new NotificationSummaryResponse(latestDto, allDtos);
    }
}
