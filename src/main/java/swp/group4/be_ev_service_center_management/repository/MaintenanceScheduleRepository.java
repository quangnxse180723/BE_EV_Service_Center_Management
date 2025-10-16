package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {

    // Đếm số lịch hẹn của khách hàng
    @Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms WHERE ms.customer.customerId = :customerId")
    long countByCustomer_CustomerId(@Param("customerId") Integer customerId);

    // Tìm lịch hẹn theo tên khách hàng
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE LOWER(ms.customer.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MaintenanceSchedule> findByCustomerNameContaining(@Param("name") String name);

    // Tìm lịch hẹn theo biển số xe
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE LOWER(ms.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :plate, '%'))")
    List<MaintenanceSchedule> findByLicensePlateContaining(@Param("plate") String plate);

    // Tìm lịch hẹn theo trạng thái
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.status = :status")
    List<MaintenanceSchedule> findByStatus(@Param("status") String status);

    // Tìm lịch hẹn theo customer ID
    List<MaintenanceSchedule> findByCustomer_CustomerId(Integer customerId);

    // Tìm lịch hẹn theo vehicle ID
    List<MaintenanceSchedule> findByVehicle_VehicleId(Integer vehicleId);
}