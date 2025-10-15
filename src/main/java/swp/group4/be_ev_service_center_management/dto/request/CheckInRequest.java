package swp.group4.be_ev_service_center_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for staff check-in vehicle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    
    private Integer scheduleId;
    private String notes;
    private String vehicleCondition; // Good, Fair, Poor
}
