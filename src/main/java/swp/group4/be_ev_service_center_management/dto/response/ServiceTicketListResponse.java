package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceTicketListResponse {
    private Integer scheduleId;
    private String customerName;
    private String vehicleModel;
    private String licensePlate;
    private String status;
    private String startTime;
}