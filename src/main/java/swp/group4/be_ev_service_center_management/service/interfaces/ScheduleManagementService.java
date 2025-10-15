package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.CreateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.ScheduleFilterRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleManagementService {
    
    /**
     * Create new maintenance schedule
     */
    ScheduleResponse createSchedule(Integer staffId, CreateScheduleRequest request);
    
    /**
     * Update existing schedule
     */
    ScheduleResponse updateSchedule(Integer staffId, UpdateScheduleRequest request);
    
    /**
     * Confirm schedule (PENDING -> CONFIRMED)
     */
    ScheduleResponse confirmSchedule(Integer staffId, Integer scheduleId);
    
    /**
     * Cancel schedule
     */
    ScheduleResponse cancelSchedule(Integer staffId, Integer scheduleId, String reason);
    
    /**
     * Get schedule detail with full information
     */
    ScheduleDetailResponse getScheduleDetailById(Integer scheduleId);
    
    /**
     * Search/Filter schedules with multiple criteria
     */
    List<ScheduleResponse> searchSchedules(Integer staffId, ScheduleFilterRequest filter);
    
    /**
     * Get schedules by date range
     */
    List<ScheduleResponse> getSchedulesByDateRange(Integer staffId, LocalDateTime from, LocalDateTime to);
    
    /**
     * Get schedules for specific week
     */
    List<ScheduleResponse> getWeeklySchedules(Integer staffId, LocalDateTime weekStart);
    
    /**
     * Get schedules for specific month
     */
    List<ScheduleResponse> getMonthlySchedules(Integer staffId, int year, int month);
    
    /**
     * Get customer's schedule history
     */
    List<ScheduleResponse> getCustomerScheduleHistory(Integer customerId);
    
    /**
     * Get vehicle's schedule history
     */
    List<ScheduleResponse> getVehicleScheduleHistory(Integer vehicleId);
}
