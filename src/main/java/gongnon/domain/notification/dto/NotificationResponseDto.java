package gongnon.domain.notification.dto;

import gongnon.domain.notification.model.NotificationRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponseDto {

    private Long notificationId;    // 알림 레코드 PK
    private Long userId;            // 유저 PK
    private String content;         // 알림 내용
    private LocalDateTime sentAt;   // 전송 시각

    // 엔티티 -> DTO 변환 헬퍼
    public static NotificationResponseDto fromEntity(NotificationRecord record) {
        return new NotificationResponseDto(
                record.getId(),
                record.getUser().getId(),
                record.getContent(),
                record.getSentAt()
        );
    }
}
