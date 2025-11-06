package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.entity.Notification;

import java.util.List;

public interface NotificationService {
    void createNotification(int userId, String message, String link);
    void createNotificationWithSender(int senderId, int receiverId, String message, String link);
    void createNotificationForApproval(int senderId, int receiverId, String message, String link, Integer recordId, Integer scheduleId);
    List<Notification> getNotifications(int userId);
    void markAsRead(int notificationId);
}
