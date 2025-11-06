package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceTicketService;

@RestController
@RequestMapping("/api/service-ticket")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceTicketController {
    private final ServiceTicketService serviceTicketService;

    /**
     * GET /api/service-ticket/{scheduleId}/detail
     * Lấy chi tiết phiếu dịch vụ cho kỹ thuật viên
     */
    @GetMapping("/{scheduleId}/detail")
    public ResponseEntity<ServiceTicketDetailResponse> getServiceTicketDetail(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(serviceTicketService.getServiceTicketDetail(scheduleId));
    }
    
    /**
     * PUT /api/service-ticket/item/{itemId}/confirm
     * Xác nhận hoàn thành một hạng mục (set status = DONE)
     */
    @PutMapping("/item/{itemId}/confirm")
    public ResponseEntity<Void> confirmItemCompletion(@PathVariable Integer itemId) {
        serviceTicketService.confirmItemCompletion(itemId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * PUT /api/service-ticket/{scheduleId}/complete
     * Xác nhận hoàn thành toàn bộ lịch hẹn (set status = COMPLETED)
     */
    @PutMapping("/{scheduleId}/complete")
    public ResponseEntity<Void> completeSchedule(@PathVariable Integer scheduleId) {
        serviceTicketService.completeSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }
}
