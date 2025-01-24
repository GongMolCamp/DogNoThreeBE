package gongnon.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotificationSummaryResponse {

    private NotificationDto latestNotification; // 가장 최근 1개
    private List<NotificationDto> allNotifications; // 전체

}
