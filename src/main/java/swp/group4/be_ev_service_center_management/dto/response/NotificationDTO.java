package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Integer notificationId;
    private Integer senderId;
    private Integer receiverId;
    private Integer relatedRecordId;
    private String type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;
}