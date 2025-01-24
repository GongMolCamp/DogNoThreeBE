package gongnon.domain.notification.repository;

import gongnon.domain.notification.model.NotificationRecord;
import gongnon.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, Long> {
    // 사용자별 알림 목록 조회
    List<NotificationRecord> findByUser(User user);

    // user별 알림 기록 모두 (내림차순)
    List<NotificationRecord> findByUserOrderBySentAtDesc(User user);

    // user별 가장 최신 알림 하나
    NotificationRecord findTopByUserOrderBySentAtDesc(User user);
}
