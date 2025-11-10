package swp.group4.be_ev_service_center_management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.security.JwtUtil;
import swp.group4.be_ev_service_center_management.service.interfaces.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public boolean register(Account account) {
        if(authRepository.existsByEmail(account.getEmail())) {
            return false;
        }
        
        // Mã hóa password
        account.setPasswordHash(encoder.encode(account.getPasswordHash()));
        
        // Lưu Account trước
        Account savedAccount = authRepository.save(account);
        
        // Nếu role là CUSTOMER, tự động tạo Customer record
        if ("CUSTOMER".equalsIgnoreCase(savedAccount.getRole())) {
            Customer customer = new Customer();
            customer.setAccount(savedAccount);
            customer.setFullName(savedAccount.getFullName());
            customer.setEmail(savedAccount.getEmail());
            customer.setPhone(null); // Chưa có phone khi đăng ký
            customer.setAddress(null); // Chưa có address khi đăng ký
            
            customerRepository.save(customer);
            
            System.out.println("✅ Customer record created for account: " + savedAccount.getEmail());
        }
        
        return true;
    }

    @Override
    public Account login(String email, String password) {
        Optional<Account> accOpt = authRepository.findByEmail(email);

        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            
            // Kiểm tra tài khoản có bị khóa không
            if(acc.getIsActive() != null && !acc.getIsActive()) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
            }
            
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
