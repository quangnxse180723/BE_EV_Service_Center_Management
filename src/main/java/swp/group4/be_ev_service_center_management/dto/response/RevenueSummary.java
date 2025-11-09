package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueSummary {
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
    private long invoiceCount;
}
