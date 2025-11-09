package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.AppointmentResponse;
import swp.group4.be_ev_service_center_management.dto.response.DashboardStatsResponse;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenanceScheduleManagementController {
    
    private final MaintenanceScheduleManagementService scheduleService;
    
    /**
     * GET /api/schedules
     * Lấy tất cả lịch hẹn
     */
    @GetMapping
    public ResponseEntity<List<MaintenanceScheduleResponse>> getAllSchedules() {
        List<MaintenanceScheduleResponse> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * GET /api/schedules/{id}
     * Lấy chi tiết lịch hẹn
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceScheduleResponse> getScheduleById(@PathVariable Integer id) {
        MaintenanceScheduleResponse schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }
    
    /**
     * PUT /api/schedules/{id}/status
     * Cập nhật trạng thái lịch hẹn (Nhân viên xử lý: check-in, hoàn tất, hủy)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MaintenanceScheduleResponse> updateScheduleStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMaintenanceScheduleRequest request) {
        MaintenanceScheduleResponse schedule = scheduleService.updateScheduleStatus(id, request);
        return ResponseEntity.ok(schedule);
    }
    
    /**
     * PUT /api/schedules/{id}/assign-technician
     * Gán kỹ thuật viên cho lịch hẹn
     */
    @PutMapping("/{id}/assign-technician")
    public ResponseEntity<MaintenanceScheduleResponse> assignTechnician(
            @PathVariable Integer id,
            @Valid @RequestBody AssignTechnicianRequest request) {
        MaintenanceScheduleResponse schedule = scheduleService.assignTechnician(id, request);
        return ResponseEntity.ok(schedule);
    }
    
    /**
     * GET /api/schedules/search/customer?name={name}
     * Tìm kiếm lịch hẹn theo tên khách hàng
     */
    @GetMapping("/search/customer")
    public ResponseEntity<List<MaintenanceScheduleResponse>> searchByCustomerName(@RequestParam String name) {
        List<MaintenanceScheduleResponse> schedules = scheduleService.searchByCustomerName(name);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * GET /api/schedules/search/vehicle?plate={plate}
     * Tìm kiếm lịch hẹn theo biển số xe
     */
    @GetMapping("/search/vehicle")
    public ResponseEntity<List<MaintenanceScheduleResponse>> searchByLicensePlate(@RequestParam String plate) {
        List<MaintenanceScheduleResponse> schedules = scheduleService.searchByLicensePlate(plate);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * GET /api/schedules/search/status?status={status}
     * Tìm kiếm lịch hẹn theo trạng thái (Chờ xác nhận, Đang thực hiện, Hoàn tất, Hủy)
     */
    @GetMapping("/search/status")
    public ResponseEntity<List<MaintenanceScheduleResponse>> searchByStatus(@RequestParam String status) {
        List<MaintenanceScheduleResponse> schedules = scheduleService.searchByStatus(status);
        return ResponseEntity.ok(schedules);
    }

    /**
     * GET /api/schedules/appointments
     * Lấy danh sách lịch hẹn cho staff
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAppointments(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(scheduleService.getAppointments(keyword));
    }

    /**
     * GET /api/schedules/dashboard/stats?date=YYYY-MM-DD
     * Lấy thống kê dashboard cho staff theo ngày
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date) {
        
        // Nếu không có date, dùng ngày hôm nay
        if (date == null) {
            date = LocalDate.now();
        }
        
        DashboardStatsResponse stats = scheduleService.getDashboardStats(date);
        return ResponseEntity.ok(stats);
    }
}