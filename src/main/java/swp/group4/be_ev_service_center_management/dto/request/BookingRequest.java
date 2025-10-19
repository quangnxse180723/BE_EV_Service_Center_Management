package swp.group4.be_ev_service_center_management.dto.request;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "customerId is required")
    private Integer customerId;

    @NotNull(message = "vehicleId is required")
    private Integer vehicleId;

    @NotNull(message = "serviceCenterId is required")
    private Integer serviceCenterId;

    @NotBlank(message = "serviceType is required")
    @Size(max = 100)
    private String serviceType;

    private List<Integer> serviceItemIds;

    @NotNull(message = "preferredStart is required")
    @Future(message = "preferredStart must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime preferredStart;

    @Positive(message = "durationMinutes must be positive")
    private Integer durationMinutes;

    @Positive(message = "estimatedCost must be positive")
    private BigDecimal estimatedCost;

    private Integer mechanicId;
    private boolean urgent;

    @Size(max = 2000)
    private String notes;

    @NotBlank(message = "contactName is required")
    @Size(max = 200)
    private String contactName;

    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "contactPhone must be valid")
    private String contactPhone;

    @Email(message = "contactEmail must be valid")
    private String contactEmail;

    @Size(max = 50)
    private String paymentPreference;

    @Size(max = 255)
    private String deviceInfo;

    @Size(max = 100)
    private String createdBy;

    public BookingRequest() {}

    // Getters / Setters

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public Integer getServiceCenterId() { return serviceCenterId; }
    public void setServiceCenterId(Integer serviceCenterId) { this.serviceCenterId = serviceCenterId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public List<Integer> getServiceItemIds() { return serviceItemIds; }
    public void setServiceItemIds(List<Integer> serviceItemIds) { this.serviceItemIds = serviceItemIds; }

    public LocalDateTime getPreferredStart() { return preferredStart; }
    public void setPreferredStart(LocalDateTime preferredStart) { this.preferredStart = preferredStart; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public Integer getMechanicId() { return mechanicId; }
    public void setMechanicId(Integer mechanicId) { this.mechanicId = mechanicId; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPaymentPreference() { return paymentPreference; }
    public void setPaymentPreference(String paymentPreference) { this.paymentPreference = paymentPreference; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}