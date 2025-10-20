package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;

import java.util.List;

public interface MaintenancePackageService {
    List<MaintenancePackageResponse> getAllMaintenancePackages();
    MaintenancePackageResponse getMaintenancePackageById(Integer id);
}

