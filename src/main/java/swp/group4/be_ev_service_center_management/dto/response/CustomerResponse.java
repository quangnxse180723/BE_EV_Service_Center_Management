package swp.group4.be_ev_service_center_management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class CustomerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String status; // e.g., "ACTIVE", "INACTIVE"
    private String preferredContactChannel;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    private List<VehicleResponse> vehicles;

    public CustomerResponse() {}

    public CustomerResponse(Integer id,
                            String fullName,
                            String email,
                            String phoneNumber,
                            String address,
                            LocalDate dateOfBirth,
                            String status,
                            String preferredContactChannel,
                            OffsetDateTime createdAt,
                            OffsetDateTime updatedAt,
                            List<VehicleResponse> vehicles) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.preferredContactChannel = preferredContactChannel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.vehicles = vehicles;
    }

    // getters / setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPreferredContactChannel() { return preferredContactChannel; }
    public void setPreferredContactChannel(String preferredContactChannel) { this.preferredContactChannel = preferredContactChannel; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<VehicleResponse> getVehicles() { return vehicles; }
    public void setVehicles(List<VehicleResponse> vehicles) { this.vehicles = vehicles; }
}