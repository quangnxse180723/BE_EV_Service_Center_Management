package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ServiceCenterRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phone;

    private Double latitude;

    private Double longitude;

    @Size(max = 100)
    private String operatingHours;

    public ServiceCenterRequest() {
    }

    public ServiceCenterRequest(String name, String address, String phone, Double latitude, Double longitude, String operatingHours) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.operatingHours = operatingHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }
}
