package swp.group4.be_ev_service_center_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for creating new maintenance schedule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequest {
    
    private Integer customerId;
    private Integer vehicleId;
    private Integer packageId;
    private LocalDateTime scheduledDate;
    private Integer slotId;
    private String notes;
}
