package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private Integer paymentId;
    private Integer invoiceId;
    private Double amount;
    private String method;
    private String status;
}