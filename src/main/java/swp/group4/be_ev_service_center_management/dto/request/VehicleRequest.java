package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class VehicleRequest {
    private Integer customerId;
    private String model;
    private String vin;
    private String licensePlate;
    private Integer currentMileage;
}