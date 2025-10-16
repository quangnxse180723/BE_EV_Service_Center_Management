package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.entity.Technician;
import swp.group4.be_ev_service_center_management.repository.TechnicianRepository;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TechnicianController {

    private final TechnicianRepository technicianRepository;

    /**
     * GET /api/technicians
     * Lấy danh sách tất cả kỹ thuật viên (để chọn khi gán)
     */
    @GetMapping
    public ResponseEntity<List<Technician>> getAllTechnicians() {
        return ResponseEntity.ok(technicianRepository.findAll());
    }
}
