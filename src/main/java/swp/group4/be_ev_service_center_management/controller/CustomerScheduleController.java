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
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;

@RestController
@RequestMapping("/api/customer/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerScheduleController {

    private final MaintenanceScheduleManagementService scheduleService;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * POST /api/customer/schedules/book
     * Đặt lịch bảo dưỡng từ phía khách hàng
     */
    @PostMapping("/book")
    public ResponseEntity<MaintenanceScheduleResponse> bookSchedule(
            @Valid @RequestBody BookScheduleRequest request,
            Authentication authentication) {

        Integer customerId = null;
        
        // 1. Ưu tiên lấy từ authentication (nếu user đã đăng nhập)
        try {
            customerId = getCustomerIdFromAuth(authentication);
        } catch (RuntimeException e) {
            // Không có authentication, bỏ qua
        }

        // 2. Nếu không có từ authentication, lấy từ request
        if (customerId == null && request.getCustomerId() != null) {
            customerId = request.getCustomerId();
        }

        // 3. Nếu vẫn không có, lấy từ vehicle (vì vehicle phải thuộc về một customer)
        if (customerId == null && request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));
            customerId = vehicle.getCustomer().getCustomerId();
        }

        // 4. Nếu vẫn không có customerId thì báo lỗi
        if (customerId == null) {
            throw new RuntimeException("Customer ID could not be determined. Please provide customerId or vehicleId.");
        }

        MaintenanceScheduleResponse response = scheduleService.bookSchedule(request, customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/customer/schedules/available-slots
     * Lấy danh sách slot thời gian còn trống theo trung tâm và ngày
     */
    @GetMapping("/available-slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Integer centerId,
            @RequestParam String date) {
        
        try {
            var availableSlots = scheduleService.getAvailableSlots(centerId, date);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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
