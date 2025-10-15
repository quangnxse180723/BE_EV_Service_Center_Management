package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.VehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import swp.group4.be_ev_service_center_management.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@RequestBody VehicleRequest req) {
        return ResponseEntity.ok(vehicleService.createVehicle(req));
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> update(@PathVariable Integer vehicleId, @RequestBody VehicleRequest req) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, req));
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> delete(@PathVariable Integer vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> get(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicle(vehicleId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<VehicleResponse>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByCustomer(customerId));
    }

    // ADMIN: lấy toàn bộ xe
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAll() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
}