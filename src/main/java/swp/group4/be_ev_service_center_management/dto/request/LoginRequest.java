package swp.group4.be_ev_service_center_management.dto.request;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
