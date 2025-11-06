package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import swp.group4.be_ev_service_center_management.dto.request.SendMessageRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChatMessageResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.ChatService;

import java.security.Principal;

/**
 * NOTE: WebSocket Controller - Xử lý real-time chat messages
 *
 * Mục đích:
 * - Nhận tin nhắn từ client qua WebSocket
 * - Lưu tin nhắn vào database qua ChatService
 * - Broadcast tin nhắn đến tất cả users trong conversation (real-time)
 *
 * Flow hoạt động:
 * 1. Client đã kết nối WebSocket (authenticated với JWT)
 * 2. Client gửi message đến /app/chat.send
 * 3. Server nhận message, verify user, lưu vào DB
 * 4. Server broadcast message đến /topic/conversation/{conversationId}
 * 5. Tất cả clients đang subscribe conversation đó nhận được message ngay lập tức
 *
 * Cách client sử dụng:
 * - Connect: stomp.connect(headers, onConnect)
 * - Subscribe: stomp.subscribe('/topic/conversation/123', onMessageReceived)
 * - Send: stomp.send('/app/chat.send', {}, JSON.stringify(message))
 */
@Controller  // NOTE: Sử dụng @Controller (không phải @RestController) cho WebSocket
@RequiredArgsConstructor
public class WebSocketChatController {

    // NOTE: SimpMessagingTemplate - Tool để gửi messages đến clients qua WebSocket
    // Sử dụng để broadcast messages đến tất cả subscribers
    private final SimpMessagingTemplate messagingTemplate;

    // NOTE: ChatService - Business logic để lưu message vào database
    private final ChatService chatService;

    // NOTE: AuthRepository - Lấy thông tin account từ database
    private final AuthRepository authRepository;

    /**
     * NOTE: Handler cho việc gửi tin nhắn real-time
     *
     * @MessageMapping("/chat.send"): Client gửi message đến /app/chat.send
     *
     * Flow:
     * 1. Client gửi JSON: {"receiverId": 2, "content": "Hello"}
     * 2. Server nhận qua WebSocket connection (đã authenticated)
     * 3. Extract email từ Principal (JWT token đã verify trong WebSocketConfig)
     * 4. Lưu message vào database qua ChatService
     * 5. Broadcast message đến /topic/conversation/{conversationId}
     * 6. Cả sender và receiver đều nhận được message ngay lập tức (nếu đang online)
     *
     * @param request Payload từ client chứa receiverId và content
     * @param headerAccessor Access WebSocket headers và session info
     * @param principal User đã authenticated (JWT), chứa username (email)
     */
    @MessageMapping("/chat.send")  // NOTE: Client gửi đến: /app/chat.send
    public void sendMessage(
            @Payload SendMessageRequest request,  // NOTE: @Payload - parse JSON từ client thành object
            SimpMessageHeaderAccessor headerAccessor,  // NOTE: Access WebSocket session và headers
            Principal principal) {  // NOTE: Principal chứa user info từ JWT authentication

        try {
            // NOTE: LOG REQUEST CHI TIẾT
            System.out.println("=== WEBSOCKET SEND MESSAGE ===");
            System.out.println("Principal (sender email): " + principal.getName());
            System.out.println("Request receiverId: " + request.getReceiverId());
            System.out.println("Request content: " + request.getContent());

            // NOTE: Lấy email của sender từ Principal
            String senderEmail = principal.getName();

            // NOTE: Tìm account của sender từ database theo email
            Account sender = authRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            System.out.println("Sender found: " + sender.getFullName() + " (ID: " + sender.getAccountId() + ")");

            // NOTE: Lưu message vào database qua ChatService
            // ChatService sẽ:
            // 1. Verify sender và receiver tồn tại
            // 2. Check quyền chat (chỉ customer-staff)
            // 3. Tìm hoặc tạo conversation
            // 4. Lưu message vào bảng chatmessage
            // 5. Return ChatMessageResponse
            ChatMessageResponse savedMessage = chatService.sendMessage(
                    sender.getAccountId(),
                    request
            );

            // NOTE: Broadcast message đến tất cả subscribers của conversation này
            // Destination: /topic/conversation/{conversationId}
            // Tất cả clients đang subscribe sẽ nhận được message này real-time
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + savedMessage.getConversationId(),  // Destination
                    savedMessage  // Payload - message object sẽ được convert sang JSON
            );

            // NOTE: Log thành công
            System.out.println("✅ Message sent from " + senderEmail +
                    " to conversation " + savedMessage.getConversationId());

        } catch (Exception e) {
            // NOTE: LOG CHI TIẾT LỖI
            System.err.println("❌ Error sending message via WebSocket:");
            System.err.println("   Error type: " + e.getClass().getName());
            System.err.println("   Error message: " + e.getMessage());
            System.err.println("   Request receiverId: " + request.getReceiverId());
            e.printStackTrace();

            // NOTE: Có thể gửi error notification đến client
            // messagingTemplate.convertAndSendToUser(
            //     principal.getName(),
            //     "/queue/errors",
            //     "Failed to send message: " + e.getMessage()
            // );
        }
    }

    /**
     * NOTE: Handler cho typing indicator (optional feature)
     *
     * Client có thể gửi notification khi user đang typing
     * Server broadcast đến người nhận để hiển thị "typing..." indicator
     *
     * @param conversationId ID của conversation
     * @param principal User đang typing
     */
    @MessageMapping("/chat.typing/{conversationId}")
    public void handleTyping(
            @DestinationVariable Integer conversationId,  // NOTE: Extract conversationId từ path
            Principal principal) {

        try {
            // NOTE: Broadcast typing indicator đến conversation
            // Frontend có thể hiển thị "User đang nhập..."
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId + "/typing",
                    principal.getName() + " is typing..."
            );
        } catch (Exception e) {
            System.err.println("Error handling typing indicator: " + e.getMessage());
        }
    }
}