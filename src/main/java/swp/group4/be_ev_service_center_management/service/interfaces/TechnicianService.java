package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.entity.Account;

import java.util.List;

public interface TechnicianService {
    List<TechnicianResponse> getAllTechnicians();
}
