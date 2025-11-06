package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Conversation;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.Staff;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Conversation> findByCustomerOrderByCreatedAtDesc(Customer customer);
    List<Conversation> findByStaffOrderByCreatedAtDesc(Staff staff);
    Optional<Conversation> findByCustomerAndStaff(Customer customer, Staff staff);
}