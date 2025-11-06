package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.SendMessageRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChatMessageResponse;
import swp.group4.be_ev_service_center_management.dto.response.ConversationResponse;

import java.util.List;

public interface ChatService {

    /**
     * Gửi tin nhắn từ sender đến receiver
     * @param senderId ID của người gửi (account_id)
     * @param request Request chứa receiverId và content
     * @return ChatMessageResponse
     */
    ChatMessageResponse sendMessage(Integer senderId, SendMessageRequest request);

    /**
     * Lấy tất cả tin nhắn trong một conversation
     * @param conversationId ID của conversation
     * @param accountId ID của người xem (để verify quyền truy cập)
     * @return List của ChatMessageResponse
     */
    List<ChatMessageResponse> getMessagesByConversation(Integer conversationId, Integer accountId);

    /**
     * Lấy tất cả conversation của một user (customer hoặc staff)
     * @param accountId ID của account
     * @return List của ConversationResponse
     */
    List<ConversationResponse> getConversationsByUser(Integer accountId);

    /**
     * Tạo hoặc lấy conversation giữa customer và staff
     * @param customerId ID của customer
     * @param staffId ID của staff
     * @return ConversationResponse
     */
    ConversationResponse getOrCreateConversation(Integer customerId, Integer staffId);

    /**
     * NOTE: TỰ ĐỘNG TẠO CONVERSATION VÀ ASSIGN STAFF CHO CUSTOMER
     *
     * Khi customer bắt đầu chat lần đầu (chưa có conversation nào),
     * hệ thống sẽ tự động assign một staff available theo logic:
     * - Ưu tiên staff có ít active conversations nhất (load balancing)
     * - Nếu tất cả staff đều busy như nhau, chọn staff đầu tiên
     *
     * @param customerId ID của customer muốn bắt đầu chat
     * @return ConversationResponse với staff đã được assign
     */
    ConversationResponse autoAssignStaffAndCreateConversation(Integer customerId);
}