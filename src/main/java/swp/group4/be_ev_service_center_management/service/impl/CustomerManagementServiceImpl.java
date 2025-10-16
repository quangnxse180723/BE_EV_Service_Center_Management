package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.UpdateCustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.CustomerManagementService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerManagementServiceImpl implements CustomerManagementService {
    
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    
    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomerResponse getCustomerById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        return mapToCustomerResponse(customer);
    }
    
    
    @Override
    @Transactional
    public CustomerResponse updateCustomer(Integer customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        // Kiểm tra email trùng (nếu thay đổi)
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
            customer.setEmail(request.getEmail());
        }
        
        // Kiểm tra phone trùng (nếu thay đổi)
        if (request.getPhone() != null && !request.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Phone number already exists: " + request.getPhone());
            }
            customer.setPhone(request.getPhone());
        }
        
        // Cập nhật các field khác
        if (request.getFullName() != null) {
            customer.setFullName(request.getFullName());
        }
        
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        return mapToCustomerResponse(updatedCustomer);
    }
    
    @Override
    @Transactional
    public void deleteCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        // Kiểm tra xem customer có xe hay lịch hẹn không
        long vehicleCount = vehicleRepository.countByCustomer_CustomerId(customerId);
        if (vehicleCount > 0) {
            throw new RuntimeException("Cannot delete customer with existing vehicles");
        }
        
        customerRepository.delete(customer);
    }
    
    @Override
    public List<CustomerResponse> searchCustomersByName(String name) {
        List<Customer> customers = customerRepository.findByFullNameContaining(name);
        return customers.stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomerResponse findByEmailOrPhone(String identifier) {
        Customer customer = customerRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new RuntimeException("Customer not found with identifier: " + identifier));
        
        return mapToCustomerResponse(customer);
    }
    
    /**
     * Helper method: Map Customer entity sang CustomerResponse DTO
     */
    private CustomerResponse mapToCustomerResponse(Customer customer) {
        // Đếm số xe của customer
        long totalVehicles = vehicleRepository.countByCustomer_CustomerId(customer.getCustomerId());
        
        // Đếm số lịch hẹn của customer
        long totalSchedules = scheduleRepository.countByCustomer_CustomerId(customer.getCustomerId());
        
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .createdAt(customer.getCreatedAt())
                .totalVehicles((int) totalVehicles)
                .totalSchedules((int) totalSchedules)
                .status("ACTIVE")  // Có thể thêm logic status nếu cần
                .build();
    }
}
