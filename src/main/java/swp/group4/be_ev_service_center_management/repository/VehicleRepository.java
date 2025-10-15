package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Vehicle;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    
    // Tìm vehicle theo customer
    List<Vehicle> findByCustomer_CustomerId(Integer customerId);
    
    // Tìm vehicle theo license plate
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    // Tìm vehicle theo VIN
    Optional<Vehicle> findByVin(String vin);
    
    // Tìm vehicle theo license plate (contains)
    List<Vehicle> findByLicensePlateContainingIgnoreCase(String plate);
}
