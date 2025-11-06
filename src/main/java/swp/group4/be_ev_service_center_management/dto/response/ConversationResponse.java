package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationResponse {
    private Integer conversationId;
    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private Integer customerAccountId;  // Account ID của customer (để staff gửi message)
    private Integer staffId;
    private String staffName;
    private String staffEmail;
    private Integer staffAccountId;  // Account ID của staff (để customer gửi message) - RECEIVERID!
    private LocalDateTime createdAt;
    private ChatMessageResponse lastMessage;
}