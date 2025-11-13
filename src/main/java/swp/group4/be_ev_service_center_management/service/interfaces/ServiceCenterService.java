package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.ServiceCenterResponse;

import java.util.List;

public interface ServiceCenterService {
    List<ServiceCenterResponse> getAllServiceCenters();
    ServiceCenterResponse getServiceCenterById(Integer id);
    ServiceCenterResponse createServiceCenter(swp.group4.be_ev_service_center_management.dto.request.ServiceCenterRequest request);
    ServiceCenterResponse updateServiceCenter(Integer id, swp.group4.be_ev_service_center_management.dto.request.ServiceCenterRequest request);
    void deleteServiceCenter(Integer id);
}

