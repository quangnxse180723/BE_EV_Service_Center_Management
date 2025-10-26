package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

@Data
public class AdminDTO {
    private Integer accountId;
    private String fullName;
    private String email;
    private String role;
    private String createdAt;
}