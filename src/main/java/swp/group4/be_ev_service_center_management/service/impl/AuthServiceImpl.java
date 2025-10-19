package swp.group4.be_ev_service_center_management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;
import swp.group4.be_ev_service_center_management.security.JwtUtil;
import swp.group4.be_ev_service_center_management.service.interfaces.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public boolean register(Account account) {
        if(authRepository.existsByEmail(account.getEmail())) {
            return false;
        }
        account.setPasswordHash(encoder.encode(account.getPasswordHash()));
        authRepository.save(account);
        return true;
    }

    @Override
    public Account login(String email, String password) {
        Optional<Account> accOpt = authRepository.findByEmail(email);

        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            if(encoder.matches(password, acc.getPasswordHash())) {
                // ✅ Token được tạo và trả về ở Controller, không lưu vào Account
                return acc;
            }
        }
        return null;
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    @Override
    public Account loadUserByEmail(String email) {
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));
    }
}
