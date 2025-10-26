package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Account, Integer> {
    List<Account> findByRole(String role);
    Optional<Account> findByEmailAndRole(String email, String role);
    boolean existsByEmailAndRole(String email, String role);
}