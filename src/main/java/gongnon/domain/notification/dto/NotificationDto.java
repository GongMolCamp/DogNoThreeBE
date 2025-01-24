package gongnon.domain.notification.dto;

import gongnon.domain.notification.model.NotificationRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
//
@Data
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private String content;
    private LocalDateTime sentAt;

    // 엔티티 -> DTO 변환
    public static NotificationDto fromEntity(NotificationRecord record) {
        return new NotificationDto(
                record.getId(),
                record.getContent(),
                record.getSentAt()
        );
    }
}
