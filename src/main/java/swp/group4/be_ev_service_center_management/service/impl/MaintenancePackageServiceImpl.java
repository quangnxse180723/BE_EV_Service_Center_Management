package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenancePackage;
import swp.group4.be_ev_service_center_management.repository.MaintenancePackageRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenancePackageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenancePackageServiceImpl implements MaintenancePackageService {

    private final MaintenancePackageRepository maintenancePackageRepository;

    @Override
    public List<MaintenancePackageResponse> getAllMaintenancePackages() {
        return maintenancePackageRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenancePackageResponse getMaintenancePackageById(Integer id) {
        MaintenancePackage maintenancePackage = maintenancePackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Package not found with id: " + id));
        return convertToResponse(maintenancePackage);
    }

    private MaintenancePackageResponse convertToResponse(MaintenancePackage maintenancePackage) {
        return MaintenancePackageResponse.builder()
                .packageId(maintenancePackage.getPackageId())
                .packageName(maintenancePackage.getName())
                .description(maintenancePackage.getDescription())
                .price(maintenancePackage.getPrice())
                .build();
    }
}
