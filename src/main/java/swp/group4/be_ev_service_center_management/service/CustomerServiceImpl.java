package swp.group4.be_ev_service_center_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.CustomerRegisterRequest;
import swp.group4.be_ev_service_center_management.dto.request.CustomerLoginRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.exception.ResourceNotFoundException;
import swp.group4.be_ev_service_center_management.repository.AccountRepository;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.service.CustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepo;
    private final AccountRepository accountRepo;

    @Override
    public CustomerResponse register(CustomerRegisterRequest req) {
        Account acc = new Account();
        acc.setEmail(req.getEmail());
        acc.setPasswordHash(req.getPassword()); // Hash thực tế!
        acc.setRole("CUSTOMER");
        accountRepo.save(acc);

        Customer c = new Customer();
        c.setFullName(req.getFullName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setAccount(acc);
        customerRepo.save(c);

        return entityToResponse(c);
    }

    @Override
    public CustomerResponse login(CustomerLoginRequest req) {
        Customer c = customerRepo.findByEmail(req.getEmail());
        if (c == null || !c.getAccount().getPasswordHash().equals(req.getPassword()))
            throw new ResourceNotFoundException("Invalid credentials");
        return entityToResponse(c);
    }

    private CustomerResponse entityToResponse(Customer c) {
        CustomerResponse res = new CustomerResponse();
        res.setCustomerId(c.getCustomerId());
        res.setFullName(c.getFullName());
        res.setEmail(c.getEmail());
        res.setPhone(c.getPhone());
        res.setAddress(c.getAddress());
        return res;
    }
}