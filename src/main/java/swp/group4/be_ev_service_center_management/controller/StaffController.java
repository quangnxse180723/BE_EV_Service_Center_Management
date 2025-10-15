package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.CheckInRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.StaffDashboardResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.StaffDashboardService;

import java.util.List;

/**
 * REST Controller for Staff operations
 * Base path: /api/staff
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffDashboardService staffDashboardService;

    /**
     * GET /api/staff/{staffId}/dashboard
     * Get dashboard statistics for staff
     */
    @GetMapping("/{staffId}/dashboard")
    public ResponseEntity<StaffDashboardResponse> getDashboard(@PathVariable Integer staffId) {
        try {
            StaffDashboardResponse response = staffDashboardService.getDashboardStats(staffId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/schedules/today
     * Get all schedules for today
     */
    @GetMapping("/{staffId}/schedules/today")
    public ResponseEntity<List<ScheduleResponse>> getTodaySchedules(@PathVariable Integer staffId) {
        try {
            List<ScheduleResponse> schedules = staffDashboardService.getTodaySchedules(staffId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/schedules?status={status}
     * Get schedules by status
     */
    @GetMapping("/{staffId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByStatus(
            @PathVariable Integer staffId,
            @RequestParam String status) {
        try {
            List<ScheduleResponse> schedules = staffDashboardService.getSchedulesByStatus(staffId, status);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/schedules/{scheduleId}
     * Get schedule detail by ID
     */
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleDetail(@PathVariable Integer scheduleId) {
        try {
            ScheduleResponse schedule = staffDashboardService.getScheduleDetail(scheduleId);
            return ResponseEntity.ok(schedule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/staff/{staffId}/check-in
     * Check-in vehicle for maintenance
     */
    @PostMapping("/{staffId}/check-in")
    public ResponseEntity<ScheduleResponse> checkInVehicle(
            @PathVariable Integer staffId,
            @RequestBody CheckInRequest request) {
        try {
            ScheduleResponse response = staffDashboardService.checkInVehicle(staffId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/staff/{staffId}/assign-technician
     * Assign technician to maintenance schedule
     */
    @PostMapping("/{staffId}/assign-technician")
    public ResponseEntity<ScheduleResponse> assignTechnician(
            @PathVariable Integer staffId,
            @RequestBody AssignTechnicianRequest request) {
        try {
            ScheduleResponse response = staffDashboardService.assignTechnician(staffId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/technicians/available
     * Get available technicians
     */
    @GetMapping("/{staffId}/technicians/available")
    public ResponseEntity<List<TechnicianResponse>> getAvailableTechnicians(@PathVariable Integer staffId) {
        try {
            List<TechnicianResponse> technicians = staffDashboardService.getAvailableTechnicians(staffId);
            return ResponseEntity.ok(technicians);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/technicians
     * Get all technicians in service center
     */
    @GetMapping("/{staffId}/technicians")
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians(@PathVariable Integer staffId) {
        try {
            List<TechnicianResponse> technicians = staffDashboardService.getAllTechnicians(staffId);
            return ResponseEntity.ok(technicians);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
