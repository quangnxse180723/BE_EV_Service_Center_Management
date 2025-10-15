package swp.group4.be_ev_service_center_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for filtering/searching schedules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleFilterRequest {
    
    private String status; // PENDING, CONFIRMED, IN_PROGRESS, DONE, CANCELLED
    private String customerName;
    private String vehiclePlate;
    private String dateFrom; // Format: yyyy-MM-dd
    private String dateTo;   // Format: yyyy-MM-dd
    private Integer packageId;
}
