package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private BigDecimal amount;
    private String method;
    private Integer invoiceId;
    private String transactionReference;
}

