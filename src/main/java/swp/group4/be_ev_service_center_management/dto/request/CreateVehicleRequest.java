package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVehicleRequest {
    @NotNull
    private Integer customerId;

    private String imageUrl;

    @NotBlank
    private String model;

    @NotBlank
    private String vin;

    @NotBlank
    private String licensePlate;

    private Integer currentMileage;

    private String lastServiceDate; // yyyy-MM-dd
}