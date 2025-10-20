package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.VehicleAssignmentResponse;

import java.util.List;

public interface TechnicianVehicleService {
    
    /**
     * Lấy danh sách xe được phân công cho kỹ thuật viên
     */
    List<VehicleAssignmentResponse> getAssignedVehicles(Integer technicianId);
    
    /**
     * Lấy danh sách xe được phân công theo trạng thái
     */
    List<VehicleAssignmentResponse> getAssignedVehiclesByStatus(Integer technicianId, String status);
}
