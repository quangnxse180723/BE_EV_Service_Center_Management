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
}
