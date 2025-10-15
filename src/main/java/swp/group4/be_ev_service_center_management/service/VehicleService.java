package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.request.VehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import java.util.List;

public interface VehicleService {
    VehicleResponse createVehicle(VehicleRequest request);
    VehicleResponse updateVehicle(Integer vehicleId, VehicleRequest request);
    void deleteVehicle(Integer vehicleId);
    VehicleResponse getVehicle(Integer vehicleId);
    List<VehicleResponse> getVehiclesByCustomer(Integer customerId);
    List<VehicleResponse> getAllVehicles(); // For admin xem toàn bộ xe
}