package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Vehicle;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    
    // Tìm xe theo model hoặc biển số
    List<Vehicle> findByModelContainingIgnoreCaseOrLicensePlateContainingIgnoreCase(String model, String licensePlate);
    
    // Đếm số xe của khách hàng
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.customer.customerId = :customerId")
    long countByCustomer_CustomerId(@Param("customerId") Integer customerId);
    
    // Tìm xe theo tên khách hàng
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.customer.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vehicle> findByCustomerNameContaining(@Param("name") String name);
}