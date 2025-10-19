package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * VehicleRequest DTO dùng để tạo hoặc cập nhật xe.
 * - Khi tạo mới: vehicleId = null
 * - Khi cập nhật: vehicleId != null
 */
public class VehicleRequest implements Serializable {

    private Integer vehicleId;

    @NotNull(message = "customerId is required when creating a vehicle")
    private Integer customerId;

    @Size(max = 20, message = "plateNumber must be at most 20 characters")
    private String plateNumber;

    @Size(max = 50, message = "model must be at most 50 characters")
    private String model;

    @Size(max = 50, message = "vin must be at most 50 characters")
    private String vin;

    @PositiveOrZero(message = "mileage must be positive or zero")
    private Integer mileage;

    @Size(max = 255, message = "imageUrl must be at most 255 characters")
    private String imageUrl;

    private LocalDate lastServiceDate; // ngày bảo dưỡng gần nhất

    public VehicleRequest() {}

    public VehicleRequest(Integer customerId,
                          String plateNumber,
                          String model,
                          String vin,
                          Integer mileage,
                          String imageUrl,
                          LocalDate lastServiceDate) {
        this.customerId = customerId;
        this.plateNumber = plateNumber;
        this.model = model;
        this.vin = vin;
        this.mileage = mileage;
        this.imageUrl = imageUrl;
        this.lastServiceDate = lastServiceDate;
    }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
}
