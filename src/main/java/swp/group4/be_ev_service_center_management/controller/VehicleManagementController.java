package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.CreateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.VehicleManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleManagementController {

    private final VehicleManagementService vehicleService;

    // Lấy tất cả xe
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    // Lấy chi tiết xe
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    // Tạo mới xe
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    // Cập nhật xe
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Integer id, @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    // Xóa xe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    // Tìm kiếm xe theo biển số hoặc tên xe
    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponse>> searchVehicles(@RequestParam String keyword) {
        return ResponseEntity.ok(vehicleService.searchVehicles(keyword));
    }

    // Tìm kiếm xe theo tên khách hàng
    @GetMapping("/search/customer")
    public ResponseEntity<List<VehicleResponse>> searchVehiclesByCustomerName(@RequestParam String name) {
        return ResponseEntity.ok(vehicleService.searchVehiclesByCustomerName(name));
    }
}