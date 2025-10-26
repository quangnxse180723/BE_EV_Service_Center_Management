package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkShiftResponse {
    private String dayOfWeek;
    private String timeRange;
    private boolean canCheckIn;
}
