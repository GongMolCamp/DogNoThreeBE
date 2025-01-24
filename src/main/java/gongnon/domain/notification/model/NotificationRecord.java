package gongnon.domain.notification.model;

import gongnon.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notification_record")
public class NotificationRecord {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 유저에게 보낸 알림인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림 내용 (뉴스 요약 문구 등)
    @Column(columnDefinition = "TEXT")
    private String content;

    // 알림을 보낸 시각
    private LocalDateTime sentAt;

    public NotificationRecord(User user, String content, LocalDateTime sentAt) {
        this.user = user;
        this.content = content;
        this.sentAt = sentAt;
    }
}
