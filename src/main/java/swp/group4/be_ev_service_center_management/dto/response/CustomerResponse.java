package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

@Data
public class CustomerResponse {
    private Integer customerId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}