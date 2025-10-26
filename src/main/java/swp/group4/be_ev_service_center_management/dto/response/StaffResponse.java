package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StaffResponse {
    private Integer staffId;
    private Integer serviceCenterId;
    private Integer accountId;
    private String fullName;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
}