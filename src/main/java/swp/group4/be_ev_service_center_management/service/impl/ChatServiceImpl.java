package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.SendMessageRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChatMessageResponse;
import swp.group4.be_ev_service_center_management.dto.response.ConversationResponse;
import swp.group4.be_ev_service_center_management.entity.*;
        import swp.group4.be_ev_service_center_management.repository.*;
        import swp.group4.be_ev_service_center_management.service.interfaces.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final AuthRepository authRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Integer senderId, SendMessageRequest request) {
        // NOTE: VALIDATION - Kiểm tra input không null
        System.out.println("=== SEND MESSAGE SERVICE ===");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Request: " + request);
        System.out.println("Receiver ID: " + request.getReceiverId());
        System.out.println("Content: " + request.getContent());

        if (senderId == null) {
            throw new RuntimeException("Sender ID must not be null");
        }

        if (request.getReceiverId() == null) {
            throw new RuntimeException("Receiver ID must not be null. Please provide receiverId in request body.");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Message content must not be empty");
        }

        // Lấy thông tin sender và receiver
        Account sender = authRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));

        Account receiver = authRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + request.getReceiverId()));

        System.out.println("Sender: " + sender.getFullName() + " (" + sender.getRole() + ")");
        System.out.println("Receiver: " + receiver.getFullName() + " (" + receiver.getRole() + ")");

        // Kiểm tra role: phải là customer-staff hoặc staff-customer
        if (!isValidChatPair(sender.getRole(), receiver.getRole())) {
            throw new RuntimeException("Chat only allowed between CUSTOMER and STAFF");
        }

        // Tìm hoặc tạo conversation
        Conversation conversation = findOrCreateConversation(sender, receiver);
        System.out.println("Conversation ID: " + conversation.getConversationId());

        // Tạo message mới
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());

        ChatMessage savedMessage = chatMessageRepository.save(message);
        System.out.println("✅ Message saved with ID: " + savedMessage.getMessageId());

        return mapToMessageResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByConversation(Integer conversationId, Integer accountId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Verify quyền truy cập
        if (!hasAccessToConversation(conversation, accountId)) {
            throw new RuntimeException("Access denied to this conversation");
        }

        List<ChatMessage> messages = chatMessageRepository.findByConversationOrderBySentAtAsc(conversation);

        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsByUser(Integer accountId) {
        Account account = authRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Conversation> conversations = new ArrayList<>();

        if ("CUSTOMER".equals(account.getRole())) {
            Customer customer = customerRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            conversations = conversationRepository.findByCustomerOrderByCreatedAtDesc(customer);
        } else if ("STAFF".equals(account.getRole())) {
            Staff staff = staffRepository.findAll().stream()
                    .filter(s -> s.getAccount() != null && s.getAccount().getAccountId().equals(accountId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            conversations = conversationRepository.findByStaffOrderByCreatedAtDesc(staff);
        }

        return conversations.stream()
                .map(this::mapToConversationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(Integer customerId, Integer staffId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Conversation conversation = conversationRepository.findByCustomerAndStaff(customer, staff)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setCustomer(customer);
                    newConv.setStaff(staff);
                    return conversationRepository.save(newConv);
                });

        return mapToConversationResponse(conversation);
    }

    /**
     * NOTE: LOGIC TỰ ĐỘNG ASSIGN STAFF CHO CUSTOMER
     *
     * Đây là method chính để customer bắt đầu chat mới
     * Flow hoạt động:
     *
     * 1. Kiểm tra customer có conversation nào chưa
     *    - Nếu đã có → return conversation hiện tại (không tạo mới)
     *    - Nếu chưa có → tiếp tục bước 2
     *
     * 2. Tìm staff available để assign (LOAD BALANCING)
     *    - Lấy tất cả staff trong hệ thống
     *    - Đếm số active conversations của mỗi staff
     *    - Chọn staff có ít conversations nhất (least loaded)
     *    - Nếu không có staff nào → throw exception
     *
     * 3. Tạo conversation mới giữa customer và staff đã chọn
     *
     * 4. Return conversation response
     *
     * @param customerId ID của customer
     * @return ConversationResponse với staff đã được auto-assign
     */
    @Override
    @Transactional
    public ConversationResponse autoAssignStaffAndCreateConversation(Integer customerId) {
        // NOTE: Bước 1 - Tìm customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // NOTE: Bước 2 - Kiểm tra customer đã có conversation chưa
        List<Conversation> existingConversations = conversationRepository.findByCustomerOrderByCreatedAtDesc(customer);

        // NOTE: Nếu customer đã có conversation, return conversation gần nhất
        // Tránh tạo nhiều conversation cho cùng 1 customer
        if (!existingConversations.isEmpty()) {
            System.out.println("Customer " + customerId + " already has conversation, returning existing one");
            return mapToConversationResponse(existingConversations.get(0));
        }

        // NOTE: Bước 3 - Customer chưa có conversation, tìm staff để assign
        // LOGIC LOAD BALANCING: Chọn staff có ít active conversations nhất

        // Lấy tất cả staff trong hệ thống
        List<Staff> allStaff = staffRepository.findAll();

        if (allStaff.isEmpty()) {
            throw new RuntimeException("No staff available in the system to assign");
        }

        // NOTE: Đếm số conversations của mỗi staff và tìm staff ít việc nhất
        Staff assignedStaff = null;
        int minConversations = Integer.MAX_VALUE;

        for (Staff staff : allStaff) {
            // Đếm số conversations hiện tại của staff này
            int conversationCount = conversationRepository.findByStaffOrderByCreatedAtDesc(staff).size();

            System.out.println("Staff ID " + staff.getStaffId() + " has " + conversationCount + " conversations");

            // NOTE: Chọn staff có ít conversations nhất (LOAD BALANCING)
            if (conversationCount < minConversations) {
                minConversations = conversationCount;
                assignedStaff = staff;
            }
        }

        // NOTE: Kiểm tra đã tìm được staff chưa
        if (assignedStaff == null) {
            // Fallback: Nếu không tìm được theo logic trên, chọn staff đầu tiên
            assignedStaff = allStaff.get(0);
            System.out.println("Using fallback: assigned first staff ID " + assignedStaff.getStaffId());
        } else {
            System.out.println("Auto-assigned Staff ID " + assignedStaff.getStaffId() +
                    " (has " + minConversations + " conversations) to Customer ID " + customerId);
        }

        // NOTE: Bước 4 - Tạo conversation mới giữa customer và assigned staff
        Conversation newConversation = new Conversation();
        newConversation.setCustomer(customer);
        newConversation.setStaff(assignedStaff);

        Conversation savedConversation = conversationRepository.save(newConversation);

        // NOTE: Log để tracking
        System.out.println("✅ Created new conversation ID " + savedConversation.getConversationId() +
                " between Customer " + customerId + " and Staff " + assignedStaff.getStaffId());

        return mapToConversationResponse(savedConversation);
    }

    // ===== Helper Methods =====

    private boolean isValidChatPair(String role1, String role2) {
        return (role1.equals("CUSTOMER") && role2.equals("STAFF")) ||
                (role1.equals("STAFF") && role2.equals("CUSTOMER"));
    }

    private Conversation findOrCreateConversation(Account sender, Account receiver) {
        Customer customer;
        Staff staff;

        if ("CUSTOMER".equals(sender.getRole())) {
            customer = customerRepository.findByAccount(sender)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            staff = staffRepository.findAll().stream()
                    .filter(s -> s.getAccount() != null && s.getAccount().getAccountId().equals(receiver.getAccountId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
        } else {
            staff = staffRepository.findAll().stream()
                    .filter(s -> s.getAccount() != null && s.getAccount().getAccountId().equals(sender.getAccountId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            customer = customerRepository.findByAccount(receiver)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        return conversationRepository.findByCustomerAndStaff(customer, staff)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setCustomer(customer);
                    newConv.setStaff(staff);
                    return conversationRepository.save(newConv);
                });
    }

    private boolean hasAccessToConversation(Conversation conversation, Integer accountId) {
        if (conversation.getCustomer().getAccount() != null &&
                conversation.getCustomer().getAccount().getAccountId().equals(accountId)) {
            return true;
        }
        if (conversation.getStaff().getAccount() != null &&
                conversation.getStaff().getAccount().getAccountId().equals(accountId)) {
            return true;
        }
        return false;
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setConversationId(message.getConversation().getConversationId());
        response.setSenderId(message.getSender().getAccountId());
        response.setSenderName(message.getSender().getFullName());
        response.setSenderRole(message.getSender().getRole());
        response.setReceiverId(message.getReceiver().getAccountId());
        response.setReceiverName(message.getReceiver().getFullName());
        response.setReceiverRole(message.getReceiver().getRole());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        return response;
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation) {
        ConversationResponse response = new ConversationResponse();
        response.setConversationId(conversation.getConversationId());
        response.setCustomerId(conversation.getCustomer().getCustomerId());
        response.setCustomerName(conversation.getCustomer().getFullName());
        response.setCustomerEmail(conversation.getCustomer().getEmail());

        // NOTE: QUAN TRỌNG - Thêm customerAccountId
        if (conversation.getCustomer().getAccount() != null) {
            response.setCustomerAccountId(conversation.getCustomer().getAccount().getAccountId());
            System.out.println("✅ Customer Account ID: " + conversation.getCustomer().getAccount().getAccountId());
        } else {
            System.err.println("⚠️ Customer.Account is NULL!");
        }

        response.setStaffId(conversation.getStaff().getStaffId());
        response.setStaffName(conversation.getStaff().getFullName());
        response.setStaffEmail(conversation.getStaff().getEmail());

        // NOTE: QUAN TRỌNG - Thêm staffAccountId (RECEIVERID!)
        if (conversation.getStaff().getAccount() != null) {
            Integer staffAccountId = conversation.getStaff().getAccount().getAccountId();
            response.setStaffAccountId(staffAccountId);
            System.out.println("✅ Staff Account ID (RECEIVERID): " + staffAccountId);
        } else {
            System.err.println("❌ ERROR: Staff.Account is NULL! Cannot get staffAccountId!");
            System.err.println("Staff ID: " + conversation.getStaff().getStaffId());
            System.err.println("Staff Name: " + conversation.getStaff().getFullName());
        }

        response.setCreatedAt(conversation.getCreatedAt());

        // Lấy tin nhắn cuối cùng
        ChatMessage lastMessage = chatMessageRepository.findFirstByConversationOrderBySentAtDesc(conversation);
        if (lastMessage != null) {
            response.setLastMessage(mapToMessageResponse(lastMessage));
        }

        System.out.println("📦 ConversationResponse mapped:");
        System.out.println("   - conversationId: " + response.getConversationId());
        System.out.println("   - staffId: " + response.getStaffId());
        System.out.println("   - staffAccountId: " + response.getStaffAccountId());

        return response;
    }
}