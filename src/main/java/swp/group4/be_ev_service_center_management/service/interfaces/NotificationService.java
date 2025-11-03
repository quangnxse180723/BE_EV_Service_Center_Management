package swp.group4.be_ev_service_center_management.service.interfaces;

import org.springframework.data.domain.Page;
import swp.group4.be_ev_service_center_management.dto.response.NotificationDTO;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import swp.group4.be_ev_service_center_management.entity.Notification;

public interface NotificationService {

    Notification createNotification(Account sender,
                                    Account receiver,
                                    MaintenanceRecord relatedRecord,
                                    String type,
                                    String title,
                                    String message);

    Page<NotificationDTO> getNotificationsForAccount(String receiverEmail, int page, int size);

    long countUnread(String receiverEmail);

    boolean markAsRead(Integer notificationId, String receiverEmail);

    int markAllAsRead(String receiverEmail);
}