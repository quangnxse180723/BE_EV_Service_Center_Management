package swp.group4.be_ev_service_center_management.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageItemDTO {
    private Integer packageId;
    private String packageName;
    private Integer itemId;
    private String itemName;
    private String itemDescription;
    private BigDecimal defaultLaborCost;
}
