package swp.group4.be_ev_service_center_management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;

import java.util.Optional;

/**
 * Data Initializer - Tự động tạo tài khoản mẫu khi khởi động ứng dụng
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=====================================");
        System.out.println("ACCOUNT IS BEING CREATED...");
        System.out.println("=====================================");
        
        // Tạo tài khoản Admin
        createAccountIfNotExists(
            "Admin",
            "admin@evcenter.com",
            "123456",
            "ADMIN"
        );
        
        // Tạo tài khoản Staff
        createAccountIfNotExists(
            "Staff",
            "staff@evcenter.com",
            "123456",
            "STAFF"
        );
        
        // Tạo tài khoản Technician
        createAccountIfNotExists(
            "Technician",
            "technician@evcenter.com",
            "123456",
            "TECHNICIAN"
        );
        
        // Tạo tài khoản Customer
        createAccountIfNotExists(
            "Customer",
            "customer@evcenter.com",
            "123456",
            "CUSTOMER"
        );
        
        System.out.println("=====================================");
        System.out.println("✅ INITIALIZATION SUCCESSFULLY!");
        System.out.println("=====================================");
    }
    
    /**
     * Tạo tài khoản nếu chưa tồn tại
     */
    private void createAccountIfNotExists(String fullName, String email, String password, String role) {
        Optional<Account> existingAccount = authRepository.findByEmail(email);
        
        if (existingAccount.isPresent()) {
            System.out.println("⚠️  ACCOUNT " + role + " (" + email + ") EXISTED - SKIP");
        } else {
            Account account = new Account();
            account.setFullName(fullName);
            account.setEmail(email);
            account.setPasswordHash(passwordEncoder.encode(password));
            account.setRole(role);
            
            authRepository.save(account);
            System.out.println("✅ CREATE ACCOUNT " + role + " SUCCESSFULLY!");
            System.out.println("   📧 Email: " + email);
            System.out.println("   🔑 Password: " + password);
        }
    }
}
