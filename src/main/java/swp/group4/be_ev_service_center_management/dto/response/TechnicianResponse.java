package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for technician information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponse {
    
    private Integer technicianId;
    private String fullName;
    private String phone;
    private String email;
    private String status; // AVAILABLE, BUSY
    private Integer activeTasksCount;
}
