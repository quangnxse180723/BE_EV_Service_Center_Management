package swp.group4.be_ev_service_center_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageChecklistItemDTO {
    private Integer checklistItemId; // Frontend gọi là checklistItemId
    private Integer packageId;
    private Integer partId;
    private String itemName;
    private String itemDescription;
    private BigDecimal laborCost; // Giá nhân công - Frontend gọi là laborCost
}
