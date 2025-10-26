package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.WorkShiftResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.WorkShiftService;
import java.util.List;

@RestController
@RequestMapping("/api/workshift")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class WorkShiftController {
    private final WorkShiftService workShiftService;

    /**
     * GET /api/workshift/technician/{technicianId}
     * Lấy danh sách ca làm cho technician (đủ 7 ngày)
     */
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<WorkShiftResponse>> getWorkShifts(@PathVariable Integer technicianId) {
        return ResponseEntity.ok(workShiftService.getWorkShiftsForTechnician(technicianId));
    }
}
