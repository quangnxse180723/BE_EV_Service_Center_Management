package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;
import java.util.Date;

@Data
public class BookingRequest {
    private Integer vehicleId;
    private Integer centerId;
    private Integer customerId;
    private Integer slotId;
    private Date scheduledDate;
    private Integer packageId;
}