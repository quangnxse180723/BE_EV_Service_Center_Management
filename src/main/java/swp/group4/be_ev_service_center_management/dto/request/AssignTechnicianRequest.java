package swp.group4.be_ev_service_center_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning technician to maintenance schedule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTechnicianRequest {
    
    private Integer scheduleId;
    private Integer technicianId;
    private String notes;
}
