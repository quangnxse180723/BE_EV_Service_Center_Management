package swp.group4.be_ev_service_center_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.LoginRequest;
import swp.group4.be_ev_service_center_management.dto.request.RegisterRequest;
import swp.group4.be_ev_service_center_management.dto.response.LoginResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.security.JwtUtil;
import swp.group4.be_ev_service_center_management.service.interfaces.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/register
     * Đăng ký tài khoản mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("📝 Register request received: " + request.getEmail());

        try {
            // Tạo Account từ RegisterRequest
            Account account = new Account();
            account.setFullName(request.getFullName());
            account.setEmail(request.getEmail());
            account.setPasswordHash(request.getPassword()); // Password chưa mã hóa, service sẽ mã hóa
            account.setRole(request.getRole());
            
            boolean success = authService.register(account);
            if (success) {
                System.out.println("✅ Register successful: " + request.getEmail());
                return ResponseEntity.ok("Registration successful");
            } else {
                System.out.println("❌ Email already exists: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email already exists");
            }
        } catch (Exception e) {
            System.err.println("❌ Register error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * POST /api/auth/login
     * Đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Gọi service để xác thực
        Account account = authService.login(request.getEmail(), request.getPassword());
        
        if (account != null) {
            // Tạo JWT token
            String token = JwtUtil.generateToken(account.getEmail());
            
            // Trả về response
            LoginResponse response = new LoginResponse(
                    token,
                    account.getEmail(),
                    account.getRole(),
                    "Login successful"
            );
            return ResponseEntity.ok(response);
        } else {
            // Đăng nhập thất bại
            LoginResponse response = new LoginResponse(
                    null,
                    null,
                    null,
                    "Invalid email or password"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * GET /api/auth/validate
     * Kiểm tra token có hợp lệ không
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Loại bỏ "Bearer " prefix
            token = token.replace("Bearer ", "");
            
            if (JwtUtil.validateToken(token)) {
                String email = JwtUtil.getEmailFromToken(token);
                return ResponseEntity.ok("Valid token for user: " + email);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token validation failed");
        }
    }

    /**
     * POST /api/auth/logout
     * Đăng xuất (với JWT, chỉ cần xóa token ở client)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // Với JWT stateless, logout chỉ cần:
        // 1. Client xóa token khỏi localStorage/cookie
        // 2. Backend có thể log hoặc blacklist token (nếu cần)
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // Optional: Thêm token vào blacklist nếu cần
            // tokenBlacklistService.addToBlacklist(token);
        }
        
        return ResponseEntity.ok("Logout successful");
    }
}
