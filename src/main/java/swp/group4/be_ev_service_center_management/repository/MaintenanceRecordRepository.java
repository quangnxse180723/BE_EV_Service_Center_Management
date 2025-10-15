package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {
    
    // Tìm record theo schedule
    Optional<MaintenanceRecord> findByMaintenanceSchedule_ScheduleId(Integer scheduleId);
    
    // Tìm records theo technician
    List<MaintenanceRecord> findByTechnician_TechnicianId(Integer technicianId);
    
    // Tìm records theo status
    List<MaintenanceRecord> findByStatus(String status);
    
    // Đếm task đang active của technician
    @Query("SELECT COUNT(mr) FROM MaintenanceRecord mr WHERE mr.technician.technicianId = :technicianId " +
           "AND mr.status IN ('IN_PROGRESS', 'PENDING', 'WAITING_APPROVE')")
    Integer countActiveTasksByTechnician(@Param("technicianId") Integer technicianId);
}
