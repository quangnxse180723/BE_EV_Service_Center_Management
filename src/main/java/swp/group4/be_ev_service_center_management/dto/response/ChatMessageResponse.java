package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Integer messageId;
    private Integer conversationId;
    private Integer senderId;
    private String senderName;
    private String senderRole;
    private Integer receiverId;
    private String receiverName;
    private String receiverRole;
    private String content;
    private LocalDateTime sentAt;
}

