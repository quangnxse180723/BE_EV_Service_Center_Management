package swp.group4.be_ev_service_center_management.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for processing a customer payment.
 * Supports paying for a schedule/order or creating a standalone payment.
 *
 * Validation notes:
 * - client should provide either scheduleId/orderId or descriptive fields as needed
 * - sensitive card data should be passed as a token (paymentToken) not raw PAN
 */
public class PaymentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "customerId is required")
    private Integer customerId;

    /**
     * Optional reference to a maintenance schedule/order the payment is for.
     */
    private Integer scheduleId;

    /**
     * Optional related vehicle id.
     */
    private Integer vehicleId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    @Size(max = 10)
    private String currency; // e.g., "VND", "USD"

    @NotBlank(message = "paymentMethod is required")
    @Pattern(regexp = "CARD|BANK_TRANSFER|MOMO|ZALOPAY|CASH", message = "unsupported paymentMethod")
    private String paymentMethod;

    /**
     * Token returned by payment gateway (card token, momo transaction id, etc).
     * Never send full card numbers in requests.
     */
    @Size(max = 255)
    private String paymentToken;

    /**
     * Non-sensitive helper info (last 4 digits) — optional.
     */
    @Size(max = 4)
    private String cardLast4;

    @Size(max = 50)
    private String cardBrand;

    @Size(max = 500)
    private String description;

    @Size(max = 1000)
    private String billingAddress;

    @Email(message = "email must be valid")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "phoneNumber must be valid")
    private String phoneNumber;

    /**
     * URLs for redirect (useful for 3rd-party web flows).
     */
    @Size(max = 1000)
    private String successUrl;

    @Size(max = 1000)
    private String failureUrl;

    /**
     * If true, store payment method for future charges (requires gateway support).
     */
    private boolean savePaymentMethod;

    /**
     * Additional arbitrary metadata to forward to payment gateway or persist.
     */
    private Map<String, String> metadata;

    /**
     * Request originator (optional).
     */
    @Size(max = 100)
    private String createdBy;

    public PaymentRequest() {}

    public PaymentRequest(Integer customerId,
                          Integer scheduleId,
                          Integer vehicleId,
                          BigDecimal amount,
                          String currency,
                          String paymentMethod,
                          String paymentToken,
                          String cardLast4,
                          String cardBrand,
                          String description,
                          String billingAddress,
                          String email,
                          String phoneNumber,
                          String successUrl,
                          String failureUrl,
                          boolean savePaymentMethod,
                          Map<String, String> metadata,
                          String createdBy) {
        this.customerId = customerId;
        this.scheduleId = scheduleId;
        this.vehicleId = vehicleId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentToken = paymentToken;
        this.cardLast4 = cardLast4;
        this.cardBrand = cardBrand;
        this.description = description;
        this.billingAddress = billingAddress;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
        this.savePaymentMethod = savePaymentMethod;
        this.metadata = metadata;
        this.createdBy = createdBy;
    }

    // getters / setters

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentToken() { return paymentToken; }
    public void setPaymentToken(String paymentToken) { this.paymentToken = paymentToken; }

    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }

    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

    public String getFailureUrl() { return failureUrl; }
    public void setFailureUrl(String failureUrl) { this.failureUrl = failureUrl; }

    public boolean isSavePaymentMethod() { return savePaymentMethod; }
    public void setSavePaymentMethod(boolean savePaymentMethod) { this.savePaymentMethod = savePaymentMethod; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}