package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private BigDecimal totalRevenue;
    private String period; // Kỳ thống kê: "08/11/2025", "Tuần 45 - 2025", etc.
    private String type; // day, week, month, year
}
