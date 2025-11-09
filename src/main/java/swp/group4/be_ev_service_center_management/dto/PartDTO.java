package swp.group4.be_ev_service_center_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartDTO {
    private Integer partId;
    private Integer centerId;
    private String name;
    private String partCode;
    private Integer quantityInStock;
    private Integer minStock;
    private BigDecimal unitPrice; // Giá vật tư - Frontend gọi là unitPrice
}
