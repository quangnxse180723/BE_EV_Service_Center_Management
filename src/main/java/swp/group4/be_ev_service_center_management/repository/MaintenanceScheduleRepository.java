package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleDTO;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {

    // ✅ THÊM METHOD NÀY
    @Query("""
        SELECT new swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleDTO(
            s.scheduleId,
            s.customer.customerId,
            s.vehicle.vehicleId,
            s.vehicle.model,
            s.vehicle.licensePlate,
            COALESCE(s.serviceCenter.centerId, 0),
            COALESCE(s.serviceCenter.name, 'Dịch vụ'),
            s.maintenancePackage.packageId,
            s.maintenancePackage.name,
            SUBSTRING(CAST(s.scheduledDate AS string), 1, 10),
            CAST(s.scheduledTime AS string),
            s.status,
            COALESCE(s.notes, '')
        )
        FROM MaintenanceSchedule s
        LEFT JOIN s.customer
        LEFT JOIN s.vehicle
        LEFT JOIN s.serviceCenter
        LEFT JOIN s.maintenancePackage
        WHERE s.customer.customerId = :customerId
        ORDER BY s.scheduledDate DESC
        """)
    List<MaintenanceScheduleDTO> findByCustomerIdWithDetails(@Param("customerId") Integer customerId);

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

    // Lấy danh sách xe được phân công cho kỹ thuật viên
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.technician.technicianId = :technicianId")
    List<MaintenanceSchedule> findByTechnician_TechnicianId(@Param("technicianId") Integer technicianId);

    // Lấy danh sách xe được phân công cho kỹ thuật viên theo trạng thái
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.technician.technicianId = :technicianId AND ms.status = :status")
    List<MaintenanceSchedule> findByTechnicianAndStatus(@Param("technicianId") Integer technicianId, @Param("status") String status);

    // Đếm số lịch hẹn theo service center và khoảng thời gian
    @Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId AND ms.scheduledDate >= :startTime AND ms.scheduledDate < :endTime")
    long countByServiceCenterIdAndScheduledDateBetween(@Param("centerId") Integer centerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}