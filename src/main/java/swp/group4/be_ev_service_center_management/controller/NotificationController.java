package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Notification;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam(required = false) Integer accountId,
            Authentication authentication) {
        
        // Ưu tiên lấy từ Authentication nếu có
        if (authentication != null && authentication.getPrincipal() instanceof Account) {
            Account account = (Account) authentication.getPrincipal();
            List<Notification> notifications = notificationService.getNotifications(account.getAccountId());
            return ResponseEntity.ok(notifications);
        }
        
        // Fallback: lấy từ query param (tạm thời cho dev)
        if (accountId != null) {
            List<Notification> notifications = notificationService.getNotifications(accountId);
            return ResponseEntity.ok(notifications);
        }
        
        return ResponseEntity.status(401).build(); // Unauthorized
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable int notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
