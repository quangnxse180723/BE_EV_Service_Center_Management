package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.MaintenanceHistoryResponse;
import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;

public interface VehicleService {
    MaintenancePackageResponse getSuggestedPackage(int vehicleId, int currentMileage);
    MaintenanceHistoryResponse getMaintenanceHistory(int vehicleId, int currentMileage);
}
