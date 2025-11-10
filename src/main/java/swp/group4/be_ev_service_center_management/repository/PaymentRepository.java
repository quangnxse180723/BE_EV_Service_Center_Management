package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByInvoice_MaintenanceRecord_MaintenanceSchedule_Customer_CustomerId(Integer customerId);
    List<Payment> findByInvoice_InvoiceId(Integer invoiceId);
    List<Payment> findByInvoice(Invoice invoice);
    
    // Lấy tổng amount từ Payment với Invoice status = PAID trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.status = 'PAID' AND p.paymentDate BETWEEN :fromDate AND :toDate")
    Double getTotalPaidAmountBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    // Aggregate Payment theo ngày
    @Query("SELECT FUNCTION('DATE', p.paymentDate) as period, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.invoice.status = 'PAID' AND p.paymentDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY FUNCTION('DATE', p.paymentDate) ORDER BY period")
    List<Object[]> aggregatePaymentByDay(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    // Aggregate Payment theo tháng
    @Query("SELECT CONCAT(FUNCTION('YEAR', p.paymentDate), '-', FUNCTION('MONTH', p.paymentDate)) as period, " +
           "COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.invoice.status = 'PAID' AND p.paymentDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY FUNCTION('YEAR', p.paymentDate), FUNCTION('MONTH', p.paymentDate) ORDER BY period")
    List<Object[]> aggregatePaymentByMonth(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    // Aggregate Payment theo năm
    @Query("SELECT FUNCTION('YEAR', p.paymentDate) as period, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.invoice.status = 'PAID' AND p.paymentDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY FUNCTION('YEAR', p.paymentDate) ORDER BY period")
    List<Object[]> aggregatePaymentByYear(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}