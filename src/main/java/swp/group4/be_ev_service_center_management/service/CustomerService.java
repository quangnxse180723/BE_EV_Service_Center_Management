
package swp.group4.be_ev_service_center_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swp.group4.be_ev_service_center_management.dto.request.CustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse register(CustomerRequest request);
    CustomerResponse login(String email, String password);
    CustomerResponse updateCustomer(Integer id, CustomerRequest request);
    void deleteCustomer(Integer id);
    CustomerResponse getCustomerById(Integer id);
    Page<CustomerResponse> searchCustomers(String q, Pageable pageable);
}