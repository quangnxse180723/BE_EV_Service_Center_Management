package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByEmail(String email);
}