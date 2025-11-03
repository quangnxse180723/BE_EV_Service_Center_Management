package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.UpdateChecklistRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChecklistResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketListResponse;
import swp.group4.be_ev_service_center_management.dto.response.VehicleAssignmentResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.ChecklistService;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceTicketService;
import swp.group4.be_ev_service_center_management.service.interfaces.TechnicianVehicleService;

import java.util.List;

@RestController
@RequestMapping("/api/technician")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TechnicianController {

    private final TechnicianVehicleService technicianVehicleService;
    private final ServiceTicketService serviceTicketService;
    private final ChecklistService checklistService;

    /**
     * GET /api/technician/{technicianId}/service-tickets
     * Lấy danh sách phiếu dịch vụ của kỹ thuật viên
     */
    @GetMapping("/{technicianId}/service-tickets")
    public ResponseEntity<List<ServiceTicketListResponse>> getServiceTickets(
            @PathVariable Integer technicianId) {
        List<ServiceTicketListResponse> tickets = serviceTicketService.getServiceTickets(technicianId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * GET /api/technician/checklist/{scheduleId}
     * Lấy checklist dựa trên scheduleId
     */
    @GetMapping("/checklist/{scheduleId}")
    public ResponseEntity<ChecklistResponse> getChecklist(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(checklistService.getChecklistByScheduleId(scheduleId));
    }

    /**
     * PUT /api/technician/checklist/{scheduleId}
     * Cập nhật checklist items
     */
    @PutMapping("/checklist/{scheduleId}")
    public ResponseEntity<ChecklistResponse> updateChecklist(
            @PathVariable Integer scheduleId,
            @RequestBody UpdateChecklistRequest request) {
        return ResponseEntity.ok(checklistService.updateChecklist(scheduleId, request));
    }

    /**
     * POST /api/technician/submit-for-approval/{scheduleId}
     * Gửi checklist cho khách hàng duyệt
     */
    @PostMapping("/submit-for-approval/{scheduleId}")
    public ResponseEntity<String> submitForApproval(@PathVariable Integer scheduleId) {
        checklistService.submitForApproval(scheduleId);
        return ResponseEntity.ok("Đã gửi cho khách hàng duyệt");
    }

    /**
     * GET /api/technician/{technicianId}/assigned-vehicles
     * Lấy danh sách xe được phân công cho kỹ thuật viên
     */
    @GetMapping("/{technicianId}/assigned-vehicles")
    public ResponseEntity<List<VehicleAssignmentResponse>> getAssignedVehicles(
            @PathVariable Integer technicianId) {
        List<VehicleAssignmentResponse> vehicles = technicianVehicleService.getAssignedVehicles(technicianId);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * GET /api/technician/{technicianId}/assigned-vehicles?status={status}
     * Lấy danh sách xe được phân công theo trạng thái
     */
    @GetMapping("/{technicianId}/assigned-vehicles/filter")
    public ResponseEntity<List<VehicleAssignmentResponse>> getAssignedVehiclesByStatus(
            @PathVariable Integer technicianId,
            @RequestParam String status) {
        List<VehicleAssignmentResponse> vehicles =
                technicianVehicleService.getAssignedVehiclesByStatus(technicianId, status);
        return ResponseEntity.ok(vehicles);
    }
}