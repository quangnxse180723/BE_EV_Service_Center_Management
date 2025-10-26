package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class StaffRequest {
    private Integer serviceCenterId;
    private Integer accountId;
    private String fullName;
    private String phone;
    private String email;
}

