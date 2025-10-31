package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentStatisticsResponse {
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private Integer year;
}

