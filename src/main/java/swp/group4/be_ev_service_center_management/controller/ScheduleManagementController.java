package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.CreateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.ScheduleFilterRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.ScheduleManagementService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Schedule Management by Staff
 * Base path: /api/staff/{staffId}/schedule-management
 */
@RestController
@RequestMapping("/api/staff/{staffId}/schedule-management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleManagementController {

    private final ScheduleManagementService scheduleManagementService;

    /**
     * POST /api/staff/{staffId}/schedule-management
     * Create new maintenance schedule
     */
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @PathVariable Integer staffId,
            @RequestBody CreateScheduleRequest request) {
        try {
            ScheduleResponse response = scheduleManagementService.createSchedule(staffId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/staff/{staffId}/schedule-management
     * Update existing schedule
     */
    @PutMapping
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Integer staffId,
            @RequestBody UpdateScheduleRequest request) {
        try {
            ScheduleResponse response = scheduleManagementService.updateSchedule(staffId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/staff/{staffId}/schedule-management/{scheduleId}/confirm
     * Confirm schedule (PENDING -> CONFIRMED)
     */
    @PostMapping("/{scheduleId}/confirm")
    public ResponseEntity<ScheduleResponse> confirmSchedule(
            @PathVariable Integer staffId,
            @PathVariable Integer scheduleId) {
        try {
            ScheduleResponse response = scheduleManagementService.confirmSchedule(staffId, scheduleId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/staff/{staffId}/schedule-management/{scheduleId}/cancel
     * Cancel schedule
     */
    @PostMapping("/{scheduleId}/cancel")
    public ResponseEntity<ScheduleResponse> cancelSchedule(
            @PathVariable Integer staffId,
            @PathVariable Integer scheduleId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : null;
            ScheduleResponse response = scheduleManagementService.cancelSchedule(staffId, scheduleId, reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/schedule-management/{scheduleId}/detail
     * Get schedule detail with full information
     */
    @GetMapping("/{scheduleId}/detail")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetail(
            @PathVariable Integer scheduleId) {
        try {
            ScheduleDetailResponse response = scheduleManagementService.getScheduleDetailById(scheduleId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/staff/{staffId}/schedule-management/search
     * Search/Filter schedules with multiple criteria
     */
    @PostMapping("/search")
    public ResponseEntity<List<ScheduleResponse>> searchSchedules(
            @PathVariable Integer staffId,
            @RequestBody ScheduleFilterRequest filter) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.searchSchedules(staffId, filter);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/schedule-management/date-range
     * Get schedules by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByDateRange(
            @PathVariable Integer staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.getSchedulesByDateRange(staffId, from, to);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/schedule-management/weekly
     * Get schedules for specific week
     */
    @GetMapping("/weekly")
    public ResponseEntity<List<ScheduleResponse>> getWeeklySchedules(
            @PathVariable Integer staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime weekStart) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.getWeeklySchedules(staffId, weekStart);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/{staffId}/schedule-management/monthly
     * Get schedules for specific month
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<ScheduleResponse>> getMonthlySchedules(
            @PathVariable Integer staffId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.getMonthlySchedules(staffId, year, month);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/schedule-management/customer/{customerId}/history
     * Get customer's schedule history
     */
    @GetMapping("/customer/{customerId}/history")
    public ResponseEntity<List<ScheduleResponse>> getCustomerScheduleHistory(
            @PathVariable Integer customerId) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.getCustomerScheduleHistory(customerId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/staff/schedule-management/vehicle/{vehicleId}/history
     * Get vehicle's schedule history
     */
    @GetMapping("/vehicle/{vehicleId}/history")
    public ResponseEntity<List<ScheduleResponse>> getVehicleScheduleHistory(
            @PathVariable Integer vehicleId) {
        try {
            List<ScheduleResponse> schedules = scheduleManagementService.getVehicleScheduleHistory(vehicleId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
