package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/schedules/{centerId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByCenter(@PathVariable Integer centerId) {
        return ResponseEntity.ok(adminService.getSchedulesByCenter(centerId));
    }
}