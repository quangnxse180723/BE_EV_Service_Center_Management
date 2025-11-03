package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleDTO;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {

    @Query("SELECT s.maintenancePackage.mileageMilestone FROM MaintenanceSchedule s WHERE s.vehicle.vehicleId = :vehicleId AND s.status = 'COMPLETED' AND s.maintenancePackage.mileageMilestone IS NOT NULL")
    List<Integer> findCompletedMaintenanceMilestonesByVehicleId(@Param("vehicleId") int vehicleId);

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

    long countByCustomer_CustomerId(@Param("customerId") Integer customerId);

    List<MaintenanceSchedule> findByStatus(@Param("status") String status);

    List<MaintenanceSchedule> findByCustomer_CustomerId(Integer customerId);

    List<MaintenanceSchedule> findByVehicle_VehicleId(Integer vehicleId);

    List<MaintenanceSchedule> findByTechnician_TechnicianId(@Param("technicianId") Integer technicianId);

    List<MaintenanceSchedule> findByTechnician_TechnicianIdAndStatus(@Param("technicianId") Integer technicianId, @Param("status") String status);

    @Query("SELECT ms FROM MaintenanceSchedule ms LEFT JOIN ms.customer c LEFT JOIN ms.vehicle v WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MaintenanceSchedule> searchAppointments(@Param("keyword") String keyword);

    @Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms WHERE ms.serviceCenter.centerId = :centerId AND ms.scheduledDate >= :startTime AND ms.scheduledDate < :endTime")
    long countByServiceCenterIdAndScheduledDateBetween(@Param("centerId") Integer centerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT ms FROM MaintenanceSchedule ms JOIN ms.customer c JOIN ms.vehicle v WHERE ms.status IN :statuses")
    List<MaintenanceSchedule> findForPayment(@Param("statuses") List<String> statuses);

    List<MaintenanceSchedule> findByCustomer_FullNameContaining(@Param("name") String name);

    List<MaintenanceSchedule> findByVehicle_LicensePlateContaining(@Param("plate") String plate);

    @Query("SELECT ms FROM MaintenanceSchedule ms " +
           "LEFT JOIN FETCH ms.maintenancePackage " +
           "LEFT JOIN FETCH ms.customer " +
           "LEFT JOIN FETCH ms.vehicle " +
           "LEFT JOIN FETCH ms.technician " +
           "WHERE ms.scheduleId = :scheduleId")
    Optional<MaintenanceSchedule> findByIdWithPackage(@Param("scheduleId") Integer scheduleId);

}