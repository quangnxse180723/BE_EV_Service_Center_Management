package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Optional<Invoice> findByMaintenanceRecord(MaintenanceRecord maintenanceRecord);
    
    // Lấy danh sách invoice đã thanh toán trong khoảng thời gian
    List<Invoice> findByStatusAndCreatedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
    
    // Tính tổng doanh thu trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(i.totalLaborCost + i.totalPartCost), 0) " +
           "FROM Invoice i " +
           "WHERE i.status = 'PAID' " +
           "AND i.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
}