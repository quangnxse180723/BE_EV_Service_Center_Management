package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceChecklist;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import java.util.List;

@Repository
public interface MaintenanceChecklistRepository extends JpaRepository<MaintenanceChecklist, Integer> {
    List<MaintenanceChecklist> findByMaintenanceRecord(MaintenanceRecord record);
}
