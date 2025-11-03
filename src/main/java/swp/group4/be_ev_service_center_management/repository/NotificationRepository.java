package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByReceiverOrderByCreatedAtDesc(Account receiver, Pageable pageable);
    Long countByReceiverAndIsReadFalse(Account receiver);
    List<Notification> findByReceiverAndType(Account receiver, String type);
}