package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueGroupDTO {
    private String period;
    private long invoices;
    private double revenue;
    private double cost;
    private double profit;
}
