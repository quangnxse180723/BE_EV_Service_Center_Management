package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.AdminDTO;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.repository.AdminRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findByRole("ADMIN")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AdminDTO getAdminById(Integer id) {
        Optional<Account> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent() && "ADMIN".equals(adminOpt.get().getRole())) {
            return toDTO(adminOpt.get());
        }
        return null;
    }

    public AdminDTO createAdmin(String fullName, String email, String password) {
        if (adminRepository.existsByEmailAndRole(email, "ADMIN")) {
            throw new RuntimeException("Admin email already exists");
        }
        Account admin = new Account();
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setPasswordHash(encoder.encode(password));
        admin.setRole("ADMIN");
        Account saved = adminRepository.save(admin);
        return toDTO(saved);
    }

    public AdminDTO updateAdmin(Integer id, String fullName, String email) {
        Account admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!"ADMIN".equals(admin.getRole())) throw new RuntimeException("Not an admin account");
        admin.setFullName(fullName);
        admin.setEmail(email);
        Account updated = adminRepository.save(admin);
        return toDTO(updated);
    }

    public void deleteAdmin(Integer id) {
        Account admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!"ADMIN".equals(admin.getRole())) throw new RuntimeException("Not an admin account");
        adminRepository.delete(admin);
    }

    public AdminDTO findAdminByEmail(String email) {
        Optional<Account> adminOpt = adminRepository.findByEmailAndRole(email, "ADMIN");
        return adminOpt.map(this::toDTO).orElse(null);
    }

    private AdminDTO toDTO(Account admin) {
        AdminDTO dto = new AdminDTO();
        dto.setAccountId(admin.getAccountId());
        dto.setFullName(admin.getFullName());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        dto.setCreatedAt(admin.getCreatedAt() != null ? admin.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return dto;
    }
}