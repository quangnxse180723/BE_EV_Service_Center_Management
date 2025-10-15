package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import java.util.List;

public interface AdminService {
    List<ScheduleResponse> getSchedulesByCenter(Integer centerId);
}