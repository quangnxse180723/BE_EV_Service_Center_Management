package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    // Tìm payments theo invoice
    List<Payment> findByInvoice_InvoiceId(Integer invoiceId);
    
    // Đếm số payment hôm nay theo service center
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.invoice.maintenanceRecord.maintenanceSchedule.serviceCenter.centerId = :centerId " +
           "AND DATE(p.paymentDate) = DATE(:date)")
    Integer countTodayPayments(@Param("centerId") Integer centerId, @Param("date") LocalDateTime date);
    
    // Tính tổng tiền thu được hôm nay
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.invoice.maintenanceRecord.maintenanceSchedule.serviceCenter.centerId = :centerId " +
           "AND DATE(p.paymentDate) = DATE(:date)")
    BigDecimal sumTodayRevenue(@Param("centerId") Integer centerId, @Param("date") LocalDateTime date);
    
    // Lấy payments theo khoảng thời gian
    @Query("SELECT p FROM Payment p " +
           "WHERE p.invoice.maintenanceRecord.maintenanceSchedule.serviceCenter.centerId = :centerId " +
           "AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsByDateRange(
        @Param("centerId") Integer centerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
