package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private Integer vehicleId;
    private String model;
    private String vin;
    private String licensePlate;
    private String imageUrl;
    private Integer currentMileage;
    private String lastServiceDate;
    private Integer customerId;
    private String customerName;
}