package swp.group4.be_ev_service_center_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for updating maintenance schedule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {
    
    private Integer scheduleId;
    private LocalDateTime scheduledDate;
    private Integer slotId;
    private Integer packageId;
    private String notes;
}
