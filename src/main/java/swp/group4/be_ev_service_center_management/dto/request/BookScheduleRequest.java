package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class BookScheduleRequest {
    private Integer customerId;
    private Integer vehicleId;
    private Integer centerId;
    private Integer slotId;  // Remove @NotNull annotation
    private String scheduledDate;  // "2025-10-28"
    private String scheduledTime;  // "09:00"
    private Integer serviceId;
    private String notes;
}

