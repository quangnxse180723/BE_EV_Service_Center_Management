package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Vehicle, Integer> {

    // Xe đến hạn bảo dưỡng theo số km
    @Query("SELECT v FROM Vehicle v WHERE v.currentMileage >= :mileageMilestone")
    List<Vehicle> findVehiclesDueByMileage(@Param("mileageMilestone") Integer mileageMilestone);

    // Xe đến hạn bảo dưỡng theo thời gian
    @Query("SELECT v FROM Vehicle v WHERE v.lastServiceDate <= :dueDate")
    List<Vehicle> findVehiclesDueByDate(@Param("dueDate") LocalDate dueDate);

    // Lịch bảo dưỡng sắp tới của khách hàng
    @Query("SELECT ms FROM MaintenanceSchedule ms WHERE ms.customer.customerId = :customerId AND ms.scheduledDate >= CURRENT_DATE")
    List<MaintenanceSchedule> findUpcomingSchedulesByCustomer(@Param("customerId") Integer customerId);
}