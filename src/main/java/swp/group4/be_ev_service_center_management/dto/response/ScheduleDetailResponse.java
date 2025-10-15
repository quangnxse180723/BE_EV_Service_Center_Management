package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for schedule detail with full information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailResponse {
    
    // Schedule info
    private Integer scheduleId;
    private LocalDateTime bookingDate;
    private LocalDateTime scheduledDate;
    private String status;
    private String notes;
    
    // Customer info
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    // Vehicle info
    private Integer vehicleId;
    private String vehiclePlate;
    private String vehicleModel;
    private String vehicleVin;
    private Integer vehicleMileage;
    
    // Time slot info
    private Integer slotId;
    private String timeSlotStart;
    private String timeSlotEnd;
    
    // Package info
    private Integer packageId;
    private String packageName;
    private String packageDescription;
    
    // Service center info
    private Integer centerId;
    private String centerName;
    
    // Technician info (if assigned)
    private Integer technicianId;
    private String technicianName;
    
    // Maintenance items (if any)
    private List<MaintenanceItemInfo> maintenanceItems;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceItemInfo {
        private String itemName;
        private String description;
        private String status;
    }
}
