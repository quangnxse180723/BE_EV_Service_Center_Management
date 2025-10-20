package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.VehicleAssignmentResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.TechnicianVehicleService;

import java.util.List;

@RestController
@RequestMapping("/api/technician")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TechnicianController {
    
    private final TechnicianVehicleService technicianVehicleService;
    
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
