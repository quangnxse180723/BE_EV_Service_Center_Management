package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Lấy danh sách xe của một customer
    @Query("SELECT v FROM Vehicle v WHERE v.customer.customerId = :customerId")
    List<Vehicle> findByCustomerId(@Param("customerId") Integer customerId);

    // Lấy chi tiết một xe của customer (dùng cho xác thực quyền chọn xe)
    @Query("SELECT v FROM Vehicle v WHERE v.vehicleId = :vehicleId AND v.customer.customerId = :customerId")
    Vehicle findByCustomerAndVehicleId(@Param("customerId") Integer customerId, @Param("vehicleId") Integer vehicleId);
}