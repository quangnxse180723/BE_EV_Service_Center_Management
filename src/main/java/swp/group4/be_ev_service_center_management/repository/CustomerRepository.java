package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Basic lookups
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Convenience alias (JpaRepository already provides findById)
    Optional<Customer> findByCustomerId(Integer customerId);

    // Existence check (useful before creating new customers)
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // Authentication helper (use only if passwords are handled/encoded appropriately)
    Optional<Customer> findByEmailAndPassword(String email, String password);

    // Search & filters
    List<Customer> findByFullNameContainingIgnoreCase(String fullNamePart);
    List<Customer> findByStatus(String status);

    // Reporting / range queries
    List<Customer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Customer> findTop10ByOrderByCreatedAtDesc();
}