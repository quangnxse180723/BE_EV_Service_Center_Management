package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.CustomerRegisterRequest;
import swp.group4.be_ev_service_center_management.dto.request.CustomerLoginRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.service.CustomerService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> login(@RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }
}