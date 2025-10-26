package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.WorkShiftResponse;
import java.util.List;

public interface WorkShiftService {
    List<WorkShiftResponse> getWorkShiftsForTechnician(Integer technicianId);
}
