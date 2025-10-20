package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenancePackageService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenancePackageController {

    private final MaintenancePackageService maintenancePackageService;

    /**
     * GET /api/services
     * Lấy tất cả gói bảo dưỡng
     */
    @GetMapping
    public ResponseEntity<List<MaintenancePackageResponse>> getAllMaintenancePackages() {
        List<MaintenancePackageResponse> packages = maintenancePackageService.getAllMaintenancePackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * GET /api/services/{id}
     * Lấy chi tiết gói bảo dưỡng
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaintenancePackageResponse> getMaintenancePackageById(@PathVariable Integer id) {
        MaintenancePackageResponse maintenancePackage = maintenancePackageService.getMaintenancePackageById(id);
        return ResponseEntity.ok(maintenancePackage);
    }
}

