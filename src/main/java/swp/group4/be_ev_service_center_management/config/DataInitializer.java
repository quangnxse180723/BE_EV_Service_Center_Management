package swp.group4.be_ev_service_center_management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.Technician;
import swp.group4.be_ev_service_center_management.entity.Staff;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;
import swp.group4.be_ev_service_center_management.repository.AuthRepository;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.TechnicianRepository;
import swp.group4.be_ev_service_center_management.repository.StaffRepository;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;

import java.util.Optional;

/**
 * Data Initializer - Tự động tạo tài khoản mẫu khi khởi động ứng dụng
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;
    private final StaffRepository staffRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=====================================");
        System.out.println("ACCOUNT IS BEING CREATED...");
        System.out.println("=====================================");

        // Ensure at least one ServiceCenter exists for staff/technician
        ServiceCenter center = serviceCenterRepository.findAll().stream().findFirst().orElseGet(() -> {
            ServiceCenter c = new ServiceCenter();
            c.setName("EV Center 1");
            c.setAddress("123 Main St");
            c.setPhone("0123456789");
            return serviceCenterRepository.save(c);
        });

        // Tạo tài khoản Admin
        createAccountIfNotExists(
            "Admin",
            "admin@evcenter.com",
            "123456",
            "ADMIN",
            null,
            null,
            null
        );
        // Tạo tài khoản Staff
        createAccountIfNotExists(
            "Staff",
            "staff@evcenter.com",
            "123456",
            "STAFF",
            center,
            null,
            null
        );
        // Tạo tài khoản Technician
        createAccountIfNotExists(
            "Technician",
            "technician@evcenter.com",
            "123456",
            "TECHNICIAN",
            null,
            center,
            null
        );
        // Tạo tài khoản Customer
        createAccountIfNotExists(
            "Customer",
            "customer@evcenter.com",
            "123456",
            "CUSTOMER",
            null,
            null,
            "0123456789"
        );
        System.out.println("=====================================");
        System.out.println("✅ INITIALIZATION SUCCESSFULLY!");
        System.out.println("=====================================");
    }

    /**
     * Tạo tài khoản nếu chưa tồn tại và lưu vào bảng tương ứng
     */
    private void createAccountIfNotExists(String fullName, String email, String password, String role, ServiceCenter staffCenter, ServiceCenter technicianCenter, String customerPhone) {
        Optional<Account> existingAccount = authRepository.findByEmail(email);
        Account account;
        if (existingAccount.isPresent()) {
            System.out.println("⚠️  ACCOUNT " + role + " (" + email + ") EXISTED - SKIP");
            account = existingAccount.get();
        } else {
            account = new Account();
            account.setFullName(fullName);
            account.setEmail(email);
            account.setPasswordHash(passwordEncoder.encode(password));
            account.setRole(role);
            authRepository.save(account);
            System.out.println("✅ CREATE ACCOUNT " + role + " SUCCESSFULLY!");
            System.out.println("   📧 Email: " + email);
            System.out.println("   🔑 Password: " + password);
        }
        // Luôn lưu vào bảng customer, staff, technician nếu không phải admin
        switch (role) {
            case "CUSTOMER" -> {
                if (customerRepository.findByAccount(account).isEmpty()) {
                    Customer customer = new Customer();
                    customer.setAccount(account);
                    customer.setFullName(fullName);
                    customer.setEmail(email);
                    customer.setPhone(customerPhone != null ? customerPhone : "0123456789");
                    customer.setAddress("123 Main St");
                    customerRepository.save(customer);
                }
            }
            case "TECHNICIAN" -> {
                if (technicianRepository.findAll().stream().noneMatch(t -> t.getAccount() != null && t.getAccount().getAccountId().equals(account.getAccountId()))) {
                    Technician technician = new Technician();
                    technician.setAccount(account);
                    technician.setFullName(fullName);
                    technician.setEmail(email);
                    technician.setPhone("0987654321");
                    technician.setServiceCenter(technicianCenter);
                    technicianRepository.save(technician);
                }
            }
            case "STAFF" -> {
                if (staffRepository.findAll().stream().noneMatch(s -> s.getAccount() != null && s.getAccount().getAccountId().equals(account.getAccountId()))) {
                    Staff staff = new Staff();
                    staff.setAccount(account);
                    staff.setFullName(fullName);
                    staff.setEmail(email);
                    staff.setPhone("0111222333");
                    staff.setServiceCenter(staffCenter);
                    staffRepository.save(staff);
                }
            }
        }
    }
}
