package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.ServiceCenterResponse;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceCenterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCenterServiceImpl implements ServiceCenterService {

    private final ServiceCenterRepository serviceCenterRepository;

    @Override
    public List<ServiceCenterResponse> getAllServiceCenters() {
        return serviceCenterRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCenterResponse getServiceCenterById(Integer id) {
        ServiceCenter center = serviceCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Center not found with id: " + id));
        return convertToResponse(center);
    }

    private ServiceCenterResponse convertToResponse(ServiceCenter center) {
        return new ServiceCenterResponse(
                center.getCenterId(),
                center.getName(),
                center.getAddress(),
                center.getPhone()
        );
    }
}

