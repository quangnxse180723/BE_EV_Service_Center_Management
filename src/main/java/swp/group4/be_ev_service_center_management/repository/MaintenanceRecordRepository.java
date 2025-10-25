package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {
    List<MaintenanceRecord> findByMaintenanceSchedule(MaintenanceSchedule schedule);
}
