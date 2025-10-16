package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.UpdateCustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.CustomerManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerManagementController {
    
    private final CustomerManagementService customerService;
    
    /**
     * GET /api/customers
     * Lấy tất cả khách hàng
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    /**
     * GET /api/customers/{id}
     * Lấy thông tin chi tiết khách hàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Integer id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    
    /**
     * PUT /api/customers/{id}
     * Cập nhật thông tin khách hàng
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * DELETE /api/customers/{id}
     * Xóa khách hàng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/customers/search?name={name}
     * Tìm kiếm khách hàng theo tên
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String name) {
        List<CustomerResponse> customers = customerService.searchCustomersByName(name);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * GET /api/customers/find?identifier={emailOrPhone}
     * Tìm khách hàng theo email hoặc số điện thoại
     */
    @GetMapping("/find")
    public ResponseEntity<CustomerResponse> findByEmailOrPhone(@RequestParam String identifier) {
        CustomerResponse customer = customerService.findByEmailOrPhone(identifier);
        return ResponseEntity.ok(customer);
    }
}
