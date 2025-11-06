package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.ChatMessage;
import swp.group4.be_ev_service_center_management.entity.Conversation;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByConversationOrderBySentAtAsc(Conversation conversation);
    ChatMessage findFirstByConversationOrderBySentAtDesc(Conversation conversation);
}
