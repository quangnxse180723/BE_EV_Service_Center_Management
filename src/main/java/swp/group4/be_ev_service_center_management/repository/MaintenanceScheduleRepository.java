package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {
    
    // Tìm lịch theo service center
    List<MaintenanceSchedule> findByServiceCenter_CenterId(Integer centerId);
    
    // Tìm lịch theo status
    List<MaintenanceSchedule> findByStatus(String status);
    
    // Tìm lịch theo service center và status
    List<MaintenanceSchedule> findByServiceCenter_CenterIdAndStatus(Integer centerId, String status);
    
    // Tìm lịch theo service center và khoảng thời gian
    List<MaintenanceSchedule> findByServiceCenter_CenterIdAndScheduledDateBetween(
        Integer centerId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Đếm lịch theo status và service center
    @Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId AND ms.status = :status")
    Integer countByServiceCenterAndStatus(@Param("centerId") Integer centerId, @Param("status") String status);
    
    // Đếm lịch hôm nay
    @Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId " +
           "AND DATE(ms.scheduledDate) = DATE(:date)")
    Integer countTodaySchedules(@Param("centerId") Integer centerId, @Param("date") LocalDateTime date);
    
    // Tìm lịch theo customer
    List<MaintenanceSchedule> findByCustomer_CustomerId(Integer customerId);
    
    // Tìm lịch theo vehicle
    List<MaintenanceSchedule> findByVehicle_VehicleId(Integer vehicleId);
    
    // Tìm lịch theo customer name (contains)
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId " +
           "AND LOWER(ms.customer.fullName) LIKE LOWER(CONCAT('%', :customerName, '%'))")
    List<MaintenanceSchedule> findByServiceCenterAndCustomerName(
        @Param("centerId") Integer centerId, 
        @Param("customerName") String customerName
    );
    
    // Tìm lịch theo vehicle plate (contains)
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId " +
           "AND LOWER(ms.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :plate, '%'))")
    List<MaintenanceSchedule> findByServiceCenterAndVehiclePlate(
        @Param("centerId") Integer centerId, 
        @Param("plate") String plate
    );
    
    // Search với nhiều điều kiện
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId " +
           "AND (:status IS NULL OR ms.status = :status) " +
           "AND (:customerName IS NULL OR LOWER(ms.customer.fullName) LIKE LOWER(CONCAT('%', :customerName, '%'))) " +
           "AND (:vehiclePlate IS NULL OR LOWER(ms.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :vehiclePlate, '%'))) " +
           "AND (:packageId IS NULL OR ms.maintenancePackage.packageId = :packageId) " +
           "AND (:dateFrom IS NULL OR ms.scheduledDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR ms.scheduledDate <= :dateTo) " +
           "ORDER BY ms.scheduledDate DESC")
    List<MaintenanceSchedule> searchSchedules(
        @Param("centerId") Integer centerId,
        @Param("status") String status,
        @Param("customerName") String customerName,
        @Param("vehiclePlate") String vehiclePlate,
        @Param("packageId") Integer packageId,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
}
