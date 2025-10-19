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

    // Simple list by customer (used by service implementations)
    List<MaintenanceSchedule> findByCustomerCustomerId(Integer customerId);

    // Tìm tất cả lịch bảo dưỡng của một khách hàng
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.customer.customerId = :customerId ORDER BY ms.scheduledTime DESC")
    List<MaintenanceSchedule> findByCustomerId(@Param("customerId") Integer customerId);

    // Tìm lịch bảo dưỡng của một khách hàng theo trạng thái
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.customer.customerId = :customerId AND ms.status = :status")
    List<MaintenanceSchedule> findByCustomerIdAndStatus(@Param("customerId") Integer customerId,
                                                       @Param("status") String status);

    // Kiểm tra xem thời gian đặt lịch đã có ai đặt chưa
    @Query("SELECT COUNT(ms) > 0 FROM MaintenanceSchedule ms " +
           "WHERE ms.serviceCenter.serviceCenterId = :serviceCenterId " +
           "AND ms.scheduledTime = :scheduledTime " +
           "AND ms.status != 'CANCELLED'")
    boolean isTimeSlotBooked(@Param("serviceCenterId") Integer serviceCenterId,
                            @Param("scheduledTime") LocalDateTime scheduledTime);

    // Tìm tất cả lịch bảo dưỡng sắp tới của khách hàng
    @Query("SELECT ms FROM MaintenanceSchedule ms " +
           "WHERE ms.customer.customerId = :customerId " +
           "AND ms.scheduledTime > :currentTime " +
           "AND ms.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY ms.scheduledTime ASC")
    List<MaintenanceSchedule> findUpcomingSchedulesByCustomerId(@Param("customerId") Integer customerId,
                                                              @Param("currentTime") LocalDateTime currentTime);

    // Tìm lịch sử bảo dưỡng của một xe
    @Query("SELECT ms FROM MaintenanceSchedule ms " +
           "WHERE ms.vehicle.vehicleId = :vehicleId " +
           "ORDER BY ms.scheduledTime DESC")
    List<MaintenanceSchedule> findMaintenanceHistoryByVehicleId(@Param("vehicleId") Integer vehicleId);

    // Kiểm tra xem khách hàng có lịch bảo dưỡng trùng thời gian không
    @Query("SELECT COUNT(ms) > 0 FROM MaintenanceSchedule ms " +
           "WHERE ms.customer.customerId = :customerId " +
           "AND ms.scheduledTime = :scheduledTime " +
           "AND ms.status != 'CANCELLED'")
    boolean hasCustomerBookedTimeSlot(@Param("customerId") Integer customerId,
                                    @Param("scheduledTime") LocalDateTime scheduledTime);

    // Tìm lịch bảo dưỡng theo khoảng thời gian
    @Query("SELECT ms FROM MaintenanceSchedule ms " +
           "WHERE ms.serviceCenter.serviceCenterId = :serviceCenterId " +
           "AND ms.scheduledTime BETWEEN :startTime AND :endTime " +
           "AND ms.status != 'CANCELLED' " +
           "ORDER BY ms.scheduledTime ASC")
    List<MaintenanceSchedule> findSchedulesBetweenDates(@Param("serviceCenterId") Integer serviceCenterId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);
}