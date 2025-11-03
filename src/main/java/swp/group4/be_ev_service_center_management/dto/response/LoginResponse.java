package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private String message;
    private Integer accountId;
    private String fullName;
    private Integer customerId;
    private Integer staffId;
    private Integer technicianId;
}
