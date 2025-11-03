package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAssignmentResponse {
    
    private Integer scheduleId;
    private String customerName;      // Khách hàng
    private String vehicleModel;      // Xe
    private String licensePlate;      // Chủ xe (nếu khác với khách hàng)
    private String status;            // Trạng thái
    private String action;            // Hành động (Chỉnh sửa, Xóa)
    private String scheduledDate;     // Ngày hẹn
}
