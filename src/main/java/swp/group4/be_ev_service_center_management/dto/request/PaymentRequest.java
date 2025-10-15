package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer invoiceId;
    private Double amount;
    private String method; // EWALLET, BANKING, CASH
}