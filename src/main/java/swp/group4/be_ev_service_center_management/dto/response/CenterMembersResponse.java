package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CenterMembersResponse {
    private List<StaffResponse> staffs;
    private List<TechnicianResponse> technicians;
}
