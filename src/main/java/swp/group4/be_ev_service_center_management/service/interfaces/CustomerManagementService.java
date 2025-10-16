package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.UpdateCustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerManagementService {
    
    /**
     * Lấy tất cả khách hàng
     */
    List<CustomerResponse> getAllCustomers();
    
    /**
     * Lấy thông tin chi tiết khách hàng
     */
    CustomerResponse getCustomerById(Integer customerId);
    
    
    /**
     * Cập nhật thông tin khách hàng
     */
    CustomerResponse updateCustomer(Integer customerId, UpdateCustomerRequest request);
    
    /**
     * Xóa khách hàng (soft delete - đổi status)
     */
    void deleteCustomer(Integer customerId);
    
    /**
     * Tìm kiếm khách hàng theo tên
     */
    List<CustomerResponse> searchCustomersByName(String name);
    
    /**
     * Tìm khách hàng theo email hoặc số điện thoại
     */
    CustomerResponse findByEmailOrPhone(String identifier);
}
