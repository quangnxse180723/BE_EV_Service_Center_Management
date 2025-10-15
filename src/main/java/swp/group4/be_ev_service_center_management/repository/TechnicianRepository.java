package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Technician;

import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Integer> {
    
    // Tìm technician theo service center
    List<Technician> findByServiceCenter_CenterId(Integer centerId);
    
    // Tìm technician theo account
    Technician findByAccount_AccountId(Integer accountId);
    
    // Đếm số technician available (không có task đang active)
    @Query("SELECT COUNT(t) FROM Technician t WHERE t.serviceCenter.centerId = :centerId " +
           "AND t.technicianId NOT IN " +
           "(SELECT DISTINCT mr.technician.technicianId FROM MaintenanceRecord mr " +
           "WHERE mr.status IN ('IN_PROGRESS', 'PENDING'))")
    Integer countAvailableTechnicians(@Param("centerId") Integer centerId);
    
    // Lấy technician available
    @Query("SELECT t FROM Technician t WHERE t.serviceCenter.centerId = :centerId " +
           "AND t.technicianId NOT IN " +
           "(SELECT DISTINCT mr.technician.technicianId FROM MaintenanceRecord mr " +
           "WHERE mr.status IN ('IN_PROGRESS', 'PENDING'))")
    List<Technician> findAvailableTechnicians(@Param("centerId") Integer centerId);
}
