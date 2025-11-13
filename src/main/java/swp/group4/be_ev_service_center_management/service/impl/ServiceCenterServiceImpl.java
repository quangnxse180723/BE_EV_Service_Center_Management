package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import swp.group4.be_ev_service_center_management.dto.response.ServiceCenterResponse;
import swp.group4.be_ev_service_center_management.dto.request.ServiceCenterRequest;
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

    @Override
    public ServiceCenterResponse createServiceCenter(ServiceCenterRequest request) {
        ServiceCenter center = new ServiceCenter();
        center.setName(request.getName());
        center.setAddress(request.getAddress());
        center.setPhone(request.getPhone());
        center.setLatitude(request.getLatitude());
        center.setLongitude(request.getLongitude());
        center.setOperatingHours(request.getOperatingHours());
        ServiceCenter saved = serviceCenterRepository.save(center);
        return convertToResponse(saved);
    }

    @Override
    public ServiceCenterResponse updateServiceCenter(Integer id, ServiceCenterRequest request) {
    ServiceCenter existing = serviceCenterRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Center not found with id: " + id));
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());
        if (request.getPhone() != null) existing.setPhone(request.getPhone());
        if (request.getLatitude() != null) existing.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) existing.setLongitude(request.getLongitude());
        if (request.getOperatingHours() != null) existing.setOperatingHours(request.getOperatingHours());
        ServiceCenter saved = serviceCenterRepository.save(existing);
        return convertToResponse(saved);
    }

    @Override
    public void deleteServiceCenter(Integer id) {
        if (!serviceCenterRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Center not found with id: " + id);
        }
        serviceCenterRepository.deleteById(id);
    }

    private ServiceCenterResponse convertToResponse(ServiceCenter center) {
        return new ServiceCenterResponse(
                center.getCenterId(),
                center.getName(),
                center.getAddress(),
                center.getPhone(),
                center.getLatitude(),
                center.getLongitude(),
                center.getOperatingHours()
        );
    }
}

