package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.TechnicianDashboardResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;

import java.time.LocalDate;
import java.util.List;

public interface TechnicianService {
    List<TechnicianResponse> getAllTechnicians();
    
    /**
     * Lấy thống kê dashboard cho technician
     * @param technicianId ID của kỹ thuật viên
     * @param date Ngày cần lấy thống kê (null = hôm nay)
     * @return TechnicianDashboardResponse
     */
    TechnicianDashboardResponse getDashboardStats(Integer technicianId, LocalDate date);
}