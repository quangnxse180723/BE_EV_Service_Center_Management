package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {
    // Kiểm tra các method dưới đây:
    List<MaintenanceRecord> findByCustomerCustomerId(Integer customerId);
    List<MaintenanceRecord> findByServiceCenterCenterId(Integer centerId);
}