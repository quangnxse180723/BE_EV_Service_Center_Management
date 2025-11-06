package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceChecklistItem;

import java.util.List;

@Repository
public interface MaintenanceChecklistItemRepository extends JpaRepository<MaintenanceChecklistItem, Integer> {
    List<MaintenanceChecklistItem> findByMaintenanceRecord_RecordId(Integer recordId);
}
