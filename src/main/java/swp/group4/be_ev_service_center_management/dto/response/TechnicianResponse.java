package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponse {
    private int technicianId;
    private Integer serviceCenterId;
    private Integer accountId;
    private String fullName;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
}
