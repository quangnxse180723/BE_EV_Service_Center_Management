package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ServiceTicketItemResponse {
    private Integer itemId;      // ID của MaintenanceItem (để update)
    private Integer stt;         // Số thứ tự hiển thị
    private String partCode;     // Mã vật tư
    private String partName;
    private BigDecimal partCost; // Giá vật tư
    private BigDecimal laborCost; // Chi phí nhân công
    private String actionStatus;
    private String processStatus;
    private String confirmAction;
}
