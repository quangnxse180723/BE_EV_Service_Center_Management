package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

@Data
public class VehicleResponse {
    private Integer vehicleId;
    private String model;
    private String vin;
    private String licensePlate;
    private Integer currentMileage;
}