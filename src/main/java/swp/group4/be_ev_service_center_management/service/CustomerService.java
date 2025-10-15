package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.request.CustomerRegisterRequest;
import swp.group4.be_ev_service_center_management.dto.request.CustomerLoginRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse register(CustomerRegisterRequest request);
    CustomerResponse login(CustomerLoginRequest request);
}