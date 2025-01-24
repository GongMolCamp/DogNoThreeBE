package gongnon.domain.notification.service;

import gongnon.domain.notification.model.NotificationRecord;
import gongnon.domain.notification.repository.NotificationRecordRepository;
import gongnon.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppNotificationService {

    private final NotificationRecordRepository notificationRecordRepository;

    /**
     * 실제 푸시/알림 전송 로직
     */
    public void sendNotificationToApp(User user, String message) {
        // 실제로는 FCM, APNs, WebSocket 등으로 전송
        log.info("[AppNotification] Sending to {}: {}", user.getName(), message);
    }
//
    /**
     * 알림 내용 DB 저장
     */
    public NotificationRecord saveNotificationRecord(User user, String content) {
        NotificationRecord record = new NotificationRecord(
                user,
                content,
                LocalDateTime.now()
        );
        return notificationRecordRepository.save(record);
    }

}
