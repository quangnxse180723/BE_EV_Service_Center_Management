package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentManagementResponse {
    private Integer scheduleId; // Thêm scheduleId để navigate
    private String customerName;
    private String vehicleName;
    private String licensePlate;
    private String scheduledDate;
    private String status; // Đã thanh toán, Chờ thanh toán
    private String action; // Xem hóa đơn, In hóa đơn
}