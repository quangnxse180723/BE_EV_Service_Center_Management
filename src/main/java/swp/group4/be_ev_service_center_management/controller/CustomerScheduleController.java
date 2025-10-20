package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;

@RestController
@RequestMapping("/api/customer/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerScheduleController {

    private final MaintenanceScheduleManagementService scheduleService;
    private final CustomerRepository customerRepository;

    /**
     * POST /api/customer/schedules/book
     * Đặt lịch bảo dưỡng từ phía khách hàng
     */
    @PostMapping("/book")
    public ResponseEntity<MaintenanceScheduleResponse> bookSchedule(
            @Valid @RequestBody BookScheduleRequest request,
            Authentication authentication) {

        // Lấy customer ID từ authentication
        Integer customerId = getCustomerIdFromAuth(authentication);

        MaintenanceScheduleResponse response = scheduleService.bookSchedule(request, customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method để lấy customer ID từ authentication
     */
    private Integer getCustomerIdFromAuth(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Account) {
            Account account = (Account) authentication.getPrincipal();
            // Tìm Customer theo Account
            Customer customer = customerRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Customer not found for this account"));
            return customer.getCustomerId();
        }
        throw new RuntimeException("Customer not authenticated");
    }
}
