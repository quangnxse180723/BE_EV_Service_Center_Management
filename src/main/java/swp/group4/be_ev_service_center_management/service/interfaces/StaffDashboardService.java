package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.CheckInRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.StaffDashboardResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;

import java.util.List;

public interface StaffDashboardService {
    
    /**
     * Get dashboard statistics for staff
     */
    StaffDashboardResponse getDashboardStats(Integer staffId);
    
    /**
     * Get all schedules for today by service center
     */
    List<ScheduleResponse> getTodaySchedules(Integer staffId);
    
    /**
     * Get schedules by status
     */
    List<ScheduleResponse> getSchedulesByStatus(Integer staffId, String status);
    
    /**
     * Get schedule detail by ID
     */
    ScheduleResponse getScheduleDetail(Integer scheduleId);
    
    /**
     * Check-in vehicle for maintenance
     */
    ScheduleResponse checkInVehicle(Integer staffId, CheckInRequest request);
    
    /**
     * Assign technician to maintenance schedule
     */
    ScheduleResponse assignTechnician(Integer staffId, AssignTechnicianRequest request);
    
    /**
     * Get available technicians
     */
    List<TechnicianResponse> getAvailableTechnicians(Integer staffId);
    
    /**
     * Get all technicians in service center
     */
    List<TechnicianResponse> getAllTechnicians(Integer staffId);
}
