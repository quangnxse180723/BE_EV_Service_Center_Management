package swp.group4.be_ev_service_center_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swp.group4.be_ev_service_center_management.dto.request.VehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    VehicleResponse updateVehicle(Integer id, VehicleRequest request);

    VehicleResponse getVehicleById(Integer id);

    List<VehicleResponse> getVehiclesByCustomer(Integer customerId);

    Page<VehicleResponse> searchVehicles(String keyword, Pageable pageable);

    VehicleResponse updateMileage(Integer id, Integer mileage);

    void deleteVehicle(Integer id);
}
