package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceHistoryResponse;
import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{vehicleId}/suggested-package")
    public ResponseEntity<MaintenancePackageResponse> getSuggestedPackage(
            @PathVariable int vehicleId,
            @RequestParam int currentMileage) {
        MaintenancePackageResponse suggestedPackage = vehicleService.getSuggestedPackage(vehicleId, currentMileage);
        if (suggestedPackage == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suggestedPackage);
    }

    @GetMapping("/{vehicleId}/maintenance-history")
    public ResponseEntity<MaintenanceHistoryResponse> getMaintenanceHistory(
            @PathVariable int vehicleId,
            @RequestParam int currentMileage) {
        MaintenanceHistoryResponse history = vehicleService.getMaintenanceHistory(vehicleId, currentMileage);
        return ResponseEntity.ok(history);
    }
}
