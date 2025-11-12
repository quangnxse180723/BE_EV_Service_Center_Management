package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.ServiceCenterResponse;
import swp.group4.be_ev_service_center_management.dto.request.ServiceCenterRequest;
import jakarta.validation.Valid;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceCenterService;

import java.util.List;

@RestController
@RequestMapping("/api/centers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceCenterController {

    private final ServiceCenterService serviceCenterService;

    /**
     * GET /api/centers
     * Lấy tất cả trung tâm dịch vụ
     */
    @GetMapping
    public ResponseEntity<List<ServiceCenterResponse>> getAllServiceCenters() {
        List<ServiceCenterResponse> centers = serviceCenterService.getAllServiceCenters();
        return ResponseEntity.ok(centers);
    }

    /**
     * GET /api/centers/{id}
     * Lấy chi tiết trung tâm dịch vụ
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCenterResponse> getServiceCenterById(@PathVariable Integer id) {
        ServiceCenterResponse center = serviceCenterService.getServiceCenterById(id);
        return ResponseEntity.ok(center);
    }

    /**
     * POST /api/centers
     * Tạo trung tâm dịch vụ mới
     */
    @PostMapping
    public ResponseEntity<ServiceCenterResponse> createServiceCenter(@Valid @RequestBody ServiceCenterRequest request) {
        ServiceCenterResponse created = serviceCenterService.createServiceCenter(request);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * PUT /api/centers/{id}
     * Cập nhật thông tin trung tâm dịch vụ
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCenterResponse> updateServiceCenter(@PathVariable Integer id, @Valid @RequestBody ServiceCenterRequest request) {
        ServiceCenterResponse updated = serviceCenterService.updateServiceCenter(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/centers/{id}
     * Xóa trung tâm dịch vụ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCenter(@PathVariable Integer id) {
        serviceCenterService.deleteServiceCenter(id);
        return ResponseEntity.noContent().build();
    }
}

