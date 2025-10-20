package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.ServiceCenterResponse;

import java.util.List;

public interface ServiceCenterService {
    List<ServiceCenterResponse> getAllServiceCenters();
    ServiceCenterResponse getServiceCenterById(Integer id);
}

