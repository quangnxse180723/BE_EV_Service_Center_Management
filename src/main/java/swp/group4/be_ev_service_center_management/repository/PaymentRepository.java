package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // Basic lookups
    Optional<Payment> findByPaymentId(Integer paymentId);
    Optional<Payment> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);

    // By customer
    List<Payment> findByCustomerCustomerId(Integer customerId);
    List<Payment> findByCustomerCustomerIdOrderByCreatedAtDesc(Integer customerId);
    List<Payment> findByCustomerCustomerIdAndStatus(Integer customerId, String status);
    Page<Payment> findByCustomerCustomerId(Integer customerId, Pageable pageable);

    // By status / date range
    List<Payment> findByStatus(String status);
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findTop10ByOrderByCreatedAtDesc();

    // Aggregation: total amount paid by a customer in a period
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.customer.customerId = :customerId AND p.createdAt BETWEEN :start AND :end")
    BigDecimal sumAmountByCustomerBetween(@Param("customerId") Integer customerId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    // Aggregation: total paid for a service center in a period (if Payment links to serviceCenter)
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE (:serviceCenterId IS NULL OR p.serviceCenter.centerId = :serviceCenterId) " +
            "AND p.createdAt BETWEEN :start AND :end")
    BigDecimal sumAmountByServiceCenterBetween(@Param("serviceCenterId") Integer serviceCenterId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}