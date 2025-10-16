package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaintenanceScheduleResponse {
    
    private Integer scheduleId;
    private String scheduledDate;
    private String status;
    private String notes;
    
    // Thông tin khách hàng
    private Integer customerId;
    private String customerName;
    
    // Thông tin xe
    private Integer vehicleId;
    private String vehicleModel;
    private String licensePlate;
    
    // Thông tin kỹ thuật viên (thêm mới)
    private Integer technicianId;
    private String technicianName;
}