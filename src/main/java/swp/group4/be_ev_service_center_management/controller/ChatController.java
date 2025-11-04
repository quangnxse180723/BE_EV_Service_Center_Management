package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.SendMessageRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChatMessageResponse;
import swp.group4.be_ev_service_center_management.dto.response.ConversationResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;
import swp.group4.be_ev_service_center_management.repository.ConversationRepository;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: REST API Controller cho Chat - Xử lý HTTP requests
 *
 * Mục đích:
 * - Cung cấp REST API để lấy lịch sử chat, danh sách conversations
 * - Bổ sung cho WebSocket (WebSocket cho real-time, REST cho lịch sử)
 * - Tất cả endpoints yêu cầu JWT authentication (qua Spring Security)
 *
 * Phân biệt REST API vs WebSocket:
 * - REST API: Lấy lịch sử messages, danh sách conversations (HTTP GET)
 * - WebSocket: Gửi/nhận messages real-time (persistent connection)
 *
 * Flow sử dụng:
 * 1. User login → nhận JWT token
 * 2. Gọi GET /api/chat/conversations với JWT → lấy danh sách cuộc hội thoại
 * 3. Click vào conversation → gọi GET /api/chat/conversation/{id}/messages
 * 4. Kết nối WebSocket với JWT → nhận messages real-time
 * 5. Gửi message qua WebSocket → lưu DB và broadcast real-time
 */
@RestController
@RequestMapping("/api/chat")  // NOTE: Base URL cho tất cả chat endpoints
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")  // NOTE: Fix CORS - dùng originPatterns thay vì origins
public class ChatController {

    // NOTE: Inject ChatService để xử lý business logic
    private final ChatService chatService;

    // NOTE: Inject AuthRepository để lấy thông tin user từ JWT
    private final AuthRepository authRepository;

    // NOTE: Inject CustomerRepository để tìm customer từ account
    private final CustomerRepository customerRepository;

    // NOTE: Inject ConversationRepository để check conversation existence
    private final ConversationRepository conversationRepository;

    /**
     * NOTE: API gửi tin nhắn qua REST (backup nếu WebSocket fail)
     *
     * Endpoint: POST /api/chat/send
     * Headers: Authorization: Bearer <JWT_TOKEN>
     * Body: {
     *   "receiverId": 2,
     *   "content": "Hello from REST API"
     * }
     *
     * Response: {
     *   "success": true,
     *   "message": "Message sent successfully",
     *   "data": { messageId, content, sentAt, ... }
     * }
     *
     * NOTE: Thông thường nên dùng WebSocket để gửi message (real-time)
     * REST API này chỉ dùng như fallback hoặc khi WebSocket không khả dụng
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody SendMessageRequest request,  // NOTE: Parse JSON body thành SendMessageRequest object
            Authentication authentication) {  // NOTE: Spring Security tự inject user đã authenticated
        try {
            // NOTE: LOG REQUEST để debug
            System.out.println("=== SEND MESSAGE REQUEST ===");
            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("Request: " + request);

            // NOTE: Lấy email từ JWT token (Spring Security đã verify JWT trong JwtFilter)
            String email = authentication.getName();

            // NOTE: Tìm account trong database theo email
            Account account = authRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // NOTE: Gọi service để lưu message vào database
            // Service sẽ validate sender/receiver, tạo conversation nếu cần
            ChatMessageResponse response = chatService.sendMessage(account.getAccountId(), request);

            // NOTE: Tạo response object với format chuẩn
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Message sent successfully");
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // NOTE: LOG FULL STACKTRACE để debug
            System.err.println("❌ ERROR in sendMessage:");
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * NOTE: API lấy tất cả messages trong một conversation
     *
     * Endpoint: GET /api/chat/conversation/{conversationId}/messages
     * Headers: Authorization: Bearer <JWT_TOKEN>
     *
     * Response: {
     *   "success": true,
     *   "data": [
     *     { messageId, content, senderId, senderName, sentAt, ... },
     *     { messageId, content, senderId, senderName, sentAt, ... }
     *   ]
     * }
     *
     * Use case:
     * - Load lịch sử chat khi user mở conversation lần đầu
     * - Load old messages khi scroll lên (pagination có thể thêm sau)
     * - Verify user có quyền xem conversation này (chỉ customer và staff của conversation)
     */
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<?> getMessagesByConversation(
            @PathVariable Integer conversationId,  // NOTE: Extract conversationId từ URL path
            Authentication authentication) {
        try {
            // NOTE: LOG REQUEST
            System.out.println("=== GET MESSAGES REQUEST ===");
            System.out.println("Conversation ID: " + conversationId);
            System.out.println("User: " + authentication.getName());

            // NOTE: Lấy thông tin user đã login từ JWT
            String email = authentication.getName();
            Account account = authRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // NOTE: Service sẽ check quyền truy cập:
            // - User phải là customer hoặc staff của conversation này
            // - Nếu không có quyền → throw exception
            List<ChatMessageResponse> messages = chatService.getMessagesByConversation(
                    conversationId,
                    account.getAccountId()
            );

            // NOTE: Return danh sách messages, sorted by sentAt ASC (từ cũ đến mới)
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", messages);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // NOTE: LOG FULL STACKTRACE
            System.err.println("❌ ERROR in getMessagesByConversation:");
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * NOTE: API lấy tất cả conversations của user hiện tại
     *
     * Endpoint: GET /api/chat/conversations
     * Headers: Authorization: Bearer <JWT_TOKEN>
     *
     * Response: {
     *   "success": true,
     *   "data": [
     *     {
     *       conversationId, customerId, customerName,
     *       staffId, staffName, createdAt,
     *       lastMessage: { content, sentAt, ... }
     *     }
     *   ]
     * }
     *
     * Use case:
     * - Hiển thị danh sách conversations trong chat sidebar
     * - Mỗi conversation show last message và timestamp
     * - Customer thấy danh sách staff đã chat
     * - Staff thấy danh sách customer đã chat
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getMyConversations(Authentication authentication) {
        try {
            // NOTE: LOG REQUEST
            System.out.println("=== GET CONVERSATIONS REQUEST ===");
            System.out.println("User: " + authentication.getName());

            // NOTE: Lấy user đang login
            String email = authentication.getName();
            Account account = authRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // NOTE: Service tự động phân biệt customer/staff và lấy conversations tương ứng
            List<ConversationResponse> conversations = chatService.getConversationsByUser(account.getAccountId());

            // NOTE: Mỗi conversation có kèm lastMessage để hiển thị preview
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", conversations);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // NOTE: LOG FULL STACKTRACE
            System.err.println("❌ ERROR in getMyConversations:");
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * NOTE: API tạo hoặc lấy conversation giữa customer và staff
     *
     * Endpoint: POST /api/chat/conversation/create?customerId=1&staffId=1
     * Headers: Authorization: Bearer <JWT_TOKEN>
     *
     * Response: {
     *   "success": true,
     *   "message": "Conversation created or retrieved successfully",
     *   "data": { conversationId, customerId, staffId, ... }
     * }
     *
     * Use case:
     * - Customer muốn chat với staff cụ thể
     * - Tạo conversation nếu chưa tồn tại
     * - Return conversation hiện có nếu đã tồn tại
     * - Đảm bảo mỗi cặp customer-staff chỉ có 1 conversation
     */
    @PostMapping("/conversation/create")
    public ResponseEntity<?> createOrGetConversation(
            @RequestParam Integer customerId,  // NOTE: Query param từ URL
            @RequestParam Integer staffId,
            Authentication authentication) {
        try {
            // NOTE: LOG REQUEST
            System.out.println("=== CREATE CONVERSATION REQUEST ===");
            System.out.println("Customer ID: " + customerId);
            System.out.println("Staff ID: " + staffId);
            System.out.println("User: " + authentication.getName());

            // NOTE: Tìm hoặc tạo conversation
            // Service check nếu conversation đã tồn tại → return existing
            // Nếu chưa → tạo mới và return
            ConversationResponse conversation = chatService.getOrCreateConversation(customerId, staffId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Conversation created or retrieved successfully");
            result.put("data", conversation);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // NOTE: LOG FULL STACKTRACE
            System.err.println("❌ ERROR in createOrGetConversation:");
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * NOTE: API TỰ ĐỘNG ASSIGN STAFF CHO CUSTOMER (FEATURE CHÍNH!)
     *
     * Endpoint: POST /api/chat/conversation/start
     * Headers: Authorization: Bearer <JWT_TOKEN>
     *
     * Response: {
     *   "success": true,
     *   "message": "Conversation started with auto-assigned staff",
     *   "data": {
     *     conversationId,
     *     customerId, customerName,
     *     staffId, staffName, staffEmail,
     *     createdAt
     *   }
     * }
     *
     * Flow hoạt động:
     * 1. Customer gọi API này khi muốn bắt đầu chat
     * 2. Hệ thống TỰ ĐỘNG tìm staff available (có ít conversations nhất)
     * 3. Tạo conversation mới giữa customer và staff đã chọn
     * 4. Return thông tin conversation để customer có thể bắt đầu chat
     *
     * Use case:
     * - Customer lần đầu vào chat, chưa biết chat với staff nào
     * - Hệ thống tự động assign staff để load balancing
     * - Nếu customer đã có conversation, return conversation hiện tại (không tạo mới)
     */
    @PostMapping("/conversation/start")
    public ResponseEntity<?> startConversationWithAutoAssign(Authentication authentication) {
        try {
            // NOTE: LOG REQUEST - QUAN TRỌNG!
            System.out.println("=== START CONVERSATION REQUEST ===");

            // NOTE: KIỂM TRA AUTHENTICATION NULL
            if (authentication == null) {
                System.err.println("❌ Authentication is NULL! Token not sent or not valid.");
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Authentication required. Please provide valid JWT token in Authorization header.");
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            System.out.println("Authenticated user: " + authentication.getName());

            // NOTE: Lấy thông tin customer từ JWT
            String email = authentication.getName();

            // NOTE: KIỂM TRA EMAIL NULL
            if (email == null || email.isEmpty()) {
                System.err.println("❌ Email is NULL or empty from authentication!");
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Invalid authentication. Email not found in token.");
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Account account = authRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            System.out.println("Account found: " + account.getAccountId() + " - " + account.getRole());

            // NOTE: Verify user là CUSTOMER
            if (!"CUSTOMER".equals(account.getRole())) {
                throw new RuntimeException("Only customers can start new conversations. Staff cannot use this endpoint.");
            }

            // NOTE: Tìm customer entity từ account
            Customer customer = customerRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            System.out.println("Customer found: " + customer.getCustomerId());

            // NOTE: Gọi service để TỰ ĐỘNG ASSIGN STAFF
            ConversationResponse conversation = chatService.autoAssignStaffAndCreateConversation(customer.getCustomerId());

            System.out.println("✅ Conversation created/retrieved: " + conversation.getConversationId());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", conversation.getConversationId() != null &&
                    conversationRepository.findById(conversation.getConversationId()).isPresent()
                    ? "Conversation started with auto-assigned staff"
                    : "Returned existing conversation");
            result.put("data", conversation);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // NOTE: LOG FULL STACKTRACE - ĐÂY LÀ CHỖ QUAN TRỌNG!
            System.err.println("❌ ERROR in startConversationWithAutoAssign:");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage() != null ? e.getMessage() : "Internal server error");
            error.put("error", e.getClass().getSimpleName());

            // NOTE: Trả về 500 để FE biết là lỗi server
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
