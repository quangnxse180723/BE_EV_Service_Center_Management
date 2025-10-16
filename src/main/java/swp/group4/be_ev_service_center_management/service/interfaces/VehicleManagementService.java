package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.CreateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleManagementService {
    List<VehicleResponse> getAllVehicles();
    VehicleResponse getVehicleById(Integer id);
    VehicleResponse createVehicle(CreateVehicleRequest request);
    VehicleResponse updateVehicle(Integer id, UpdateVehicleRequest request);
    void deleteVehicle(Integer id);
    List<VehicleResponse> searchVehicles(String keyword);
    List<VehicleResponse> searchVehiclesByCustomerName(String customerName);
}