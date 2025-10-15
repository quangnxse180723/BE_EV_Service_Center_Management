package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import java.util.List;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {
    List<MaintenanceSchedule> findByCustomerCustomerId(Integer customerId);
    List<MaintenanceSchedule> findByServiceCenterCenterId(Integer centerId);
}