package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.entity.Account;

import java.util.Optional;

public interface AuthService {
    boolean register(Account account);
    Account login(String email, String password);
    Optional<Account> findByEmail(String email);
    Account loadUserByEmail(String email);
}
