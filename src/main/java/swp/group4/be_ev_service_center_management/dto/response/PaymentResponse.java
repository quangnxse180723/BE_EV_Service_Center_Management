package swp.group4.be_ev_service_center_management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public class PaymentResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer customerId;
    private Integer scheduleId;
    private Integer vehicleId;

    private BigDecimal amount;
    private String currency;
    private String paymentMethod; // e.g., CARD, MOMO, BANK_TRANSFER, CASH

    private String status; // e.g., "SUCCESS", "PENDING", "FAILED", "REFUNDED"
    private String gatewayTransactionId; // id from payment gateway
    private String paymentTokenMasked; // masked token or reference (not full PAN)
    private String cardLast4;
    private String cardBrand;

    private String receiptUrl;

    private BigDecimal refundedAmount;
    private String refundStatus; // e.g., "NONE", "PARTIAL", "FULL"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime paymentAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime refundedAt;

    private Map<String, String> metadata;

    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    public PaymentResponse() {}

    public PaymentResponse(Integer id,
                           Integer customerId,
                           Integer scheduleId,
                           Integer vehicleId,
                           BigDecimal amount,
                           String currency,
                           String paymentMethod,
                           String status,
                           String gatewayTransactionId,
                           String paymentTokenMasked,
                           String cardLast4,
                           String cardBrand,
                           String receiptUrl,
                           BigDecimal refundedAmount,
                           String refundStatus,
                           OffsetDateTime paymentAt,
                           OffsetDateTime refundedAt,
                           Map<String, String> metadata,
                           String createdBy,
                           String updatedBy,
                           OffsetDateTime createdAt,
                           OffsetDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.scheduleId = scheduleId;
        this.vehicleId = vehicleId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.gatewayTransactionId = gatewayTransactionId;
        this.paymentTokenMasked = paymentTokenMasked;
        this.cardLast4 = cardLast4;
        this.cardBrand = cardBrand;
        this.receiptUrl = receiptUrl;
        this.refundedAmount = refundedAmount;
        this.refundStatus = refundStatus;
        this.paymentAt = paymentAt;
        this.refundedAt = refundedAt;
        this.metadata = metadata;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters / Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }

    public String getPaymentTokenMasked() { return paymentTokenMasked; }
    public void setPaymentTokenMasked(String paymentTokenMasked) { this.paymentTokenMasked = paymentTokenMasked; }

    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }

    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public OffsetDateTime getPaymentAt() { return paymentAt; }
    public void setPaymentAt(OffsetDateTime paymentAt) { this.paymentAt = paymentAt; }

    public OffsetDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(OffsetDateTime refundedAt) { this.refundedAt = refundedAt; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}