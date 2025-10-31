package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentResponse {
    private String id;
    private String dateTime;
    private String licensePlate;
    private String customerName;
    private String status;
    private String action;
    private String centerName;
}
