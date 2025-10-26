package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.StaffRequest;
import swp.group4.be_ev_service_center_management.dto.response.StaffResponse;

import java.util.List;

public interface StaffService {
    List<StaffResponse> getAllStaffs();
    StaffResponse addStaff(StaffRequest request);
    StaffResponse updateStaff(Integer staffId, StaffRequest request);
}

