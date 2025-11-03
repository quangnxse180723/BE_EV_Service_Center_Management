package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.NotificationDTO;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy danh sách notification của user hiện tại (paging).
     * - currentUserEmail được lấy từ Spring Security principal (username = email).
     */
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> listNotifications(
            @AuthenticationPrincipal(expression = "username") String currentUserEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (currentUserEmail == null) {
            return ResponseEntity.status(401).body(new PageImpl<>(java.util.List.of()));
        }

        Page<NotificationDTO> result = notificationService.getNotificationsForAccount(currentUserEmail,
                Math.max(0, page), Math.max(1, Math.min(size, 100)));
        return ResponseEntity.ok(result);
    }

    /**
     * Trả về số lượng notification chưa đọc của user hiện tại.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal(expression = "username") String currentUserEmail) {
        if (currentUserEmail == null) return ResponseEntity.status(401).build();
        long count = notificationService.countUnread(currentUserEmail);
        return ResponseEntity.ok(count);
    }

    /**
     * Đánh dấu 1 notification là đã đọc.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Integer id,
                                         @AuthenticationPrincipal(expression = "username") String currentUserEmail) {
        if (currentUserEmail == null) return ResponseEntity.status(401).build();
        boolean ok = notificationService.markAsRead(id, currentUserEmail);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    /**
     * Đánh dấu tất cả notification của user là đã đọc.
     * Trả về số lượng notification đã đổi trạng thái.
     */
    @PutMapping("/read-all")
    public ResponseEntity<Integer> markAllRead(@AuthenticationPrincipal(expression = "username") String currentUserEmail) {
        if (currentUserEmail == null) return ResponseEntity.status(401).build();
        int changed = notificationService.markAllAsRead(currentUserEmail);
        return ResponseEntity.ok(changed);
    }
}