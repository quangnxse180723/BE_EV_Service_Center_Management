package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for maintenance schedule information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    
    private Integer scheduleId;
    private Integer vehicleId;
    private String vehiclePlateNumber;
    private String vehicleModel;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime scheduledDate;
    private String timeSlot;
    private String packageName;
    private String status;
    private String technicianName;
    private Integer technicianId;
    private LocalDateTime createdAt;
    private LocalDateTime bookingDate;
    private Integer centerId;
}
