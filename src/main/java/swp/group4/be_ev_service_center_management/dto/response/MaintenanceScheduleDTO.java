package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MaintenanceScheduleDTO {
    private Integer scheduleId;
    private Integer customerId;
    
    // ✅ Vehicle info - CẦN CÓ
    private Integer vehicleId;
    private String vehicleModel;        // ← CẦN MAPPING
    private String vehicleLicensePlate; // ← CẦN MAPPING
    
    // ✅ Center info - CẦN CÓ
    private Integer centerId;
    private String centerName;          // ← CẦN MAPPING
    
    // ✅ Service info
    private Integer serviceId;
    private String serviceName;         // ← CẦN MAPPING
    
    private String scheduledDate;
    private String scheduledTime;
    private String status;
    private String notes;

    // Constructor đầy đủ để dùng trong @Query
    public MaintenanceScheduleDTO(
        Integer scheduleId,
        Integer customerId,
        Integer vehicleId,
        String vehicleModel,
        String vehicleLicensePlate,
        Integer centerId,
        String centerName,
        Integer serviceId,
        String serviceName,
        String scheduledDate,
        String scheduledTime,
        String status,
        String notes
    ) {
        this.scheduleId = scheduleId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.centerId = centerId;
        this.centerName = centerName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.notes = notes;
    }
}
