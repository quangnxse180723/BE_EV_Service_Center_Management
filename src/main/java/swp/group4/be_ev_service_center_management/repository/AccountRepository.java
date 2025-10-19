
        package swp.group4.be_ev_service_center_management.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Account;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Basic lookups
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByAccountId(Integer accountId);

    // Existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Token / reset helpers
    Optional<Account> findByResetToken(String resetToken);

    // Role / status filters
    List<Account> findByRole(String role);
    Page<Account> findByRole(String role, Pageable pageable);
    List<Account> findByStatus(String status);
    List<Account> findByRoleAndStatus(String role, String status);

    // Relationship lookup (if Account links to Customer)
    List<Account> findByCustomerCustomerId(Integer customerId);

    // Search helpers
    List<Account> findByUsernameContainingIgnoreCase(String usernamePart);
    Page<Account> findByUsernameContainingIgnoreCase(String usernamePart, Pageable pageable);

    // Recent / reporting
    List<Account> findTop10ByOrderByCreatedAtDesc();
    List<Account> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByRole(String role);

    // Example custom query for pending admin approvals
    @Query("SELECT a FROM Account a WHERE a.role = 'ADMIN' AND a.status = 'PENDING' ORDER BY a.createdAt ASC")
    List<Account> findPendingAdminApprovals();
}