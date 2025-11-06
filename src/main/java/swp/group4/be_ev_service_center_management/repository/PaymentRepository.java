package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByInvoice_MaintenanceRecord_MaintenanceSchedule_Customer_CustomerId(Integer customerId);
    List<Payment> findByInvoice_InvoiceId(Integer invoiceId);
}