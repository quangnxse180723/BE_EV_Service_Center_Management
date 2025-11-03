package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MaintenancePackageResponse {
    private Integer packageId;
    private String packageName;
    private String description;
    private BigDecimal price;
    private String reason;
}
