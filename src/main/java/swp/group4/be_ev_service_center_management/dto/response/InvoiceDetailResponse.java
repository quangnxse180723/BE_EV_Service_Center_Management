package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceDetailResponse {
    private Integer scheduleId;
    private String customerName;
    private String vehicleName;
    private String licensePlate;
    private String maintenanceType;
    private String repairNote;
    private String detailButtonLabel;
    private String totalCost;
    private String paymentMethod;
    private String status;
}