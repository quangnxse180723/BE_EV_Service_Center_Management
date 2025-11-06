package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Notification;
import swp.group4.be_ev_service_center_management.repository.AccountRepository;
import swp.group4.be_ev_service_center_management.repository.NotificationRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @Override
    public void createNotification(int userId, String message, String link) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setSender(user); // Sender = Receiver (tạm thời)
        notification.setReceiver(user);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setType("SYSTEM");
        notificationRepository.save(notification);
    }

    @Override
    public void createNotificationWithSender(int senderId, int receiverId, String message, String link) {
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Notification notification = new Notification();
        notification.setUser(receiver); // user = người nhận
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setType("SYSTEM");
        notificationRepository.save(notification);
    }

    @Override
    public void createNotificationForApproval(int senderId, int receiverId, String message, String link, 
                                              Integer recordId, Integer scheduleId) {
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setType("APPROVAL_REQUEST");
        notification.setRelatedRecordId(recordId);
        notification.setRelatedScheduleId(scheduleId);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifications(int userId) {
        return notificationRepository.findByUser_AccountIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
