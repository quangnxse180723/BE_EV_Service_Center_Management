package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ServiceTicketDetailResponse {
    // Thông tin chung
    private String customerName;
    private String vehicleName;
    private String licensePlate;
    private String appointmentDateTime;
    
    // ID của checklist để phê duyệt
    private Integer checklistId;
    
    // Danh sách phụ tùng/checklist
    private List<ServiceTicketItemResponse> items;

    // Có thể bổ sung các trường khác nếu cần
}
