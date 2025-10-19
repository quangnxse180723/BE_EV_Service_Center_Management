
package swp.group4.be_ev_service_center_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import swp.group4.be_ev_service_center_management.dto.request.CustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse register(CustomerRequest request) {
        if (request == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Request required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }
        String email = request.getEmail().trim().toLowerCase();

        customerRepository.findByEmail(email).ifPresent(c -> {
            throw new ResponseStatusException(CONFLICT, "Email already registered");
        });

        Customer c = new Customer();
        c.setFullName(request.getFullName());
        c.setEmail(email);
        c.setPhone(request.getPhone());
        c.setAddress(request.getAddress());
        // createdAt handled by @CreationTimestamp; but set if null for immediate availability
        if (c.getCreatedAt() == null) {
            c.setCreatedAt(LocalDateTime.now());
        }

        Customer saved = customerRepository.save(c);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }
        Customer c = customerRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));
        // Note: password is not checked here because Customer entity does not store password.
        return toResponse(c);
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim().toLowerCase();
            if (!email.equalsIgnoreCase(Optional.ofNullable(existing.getEmail()).orElse(""))) {
                customerRepository.findByEmail(email).ifPresent(conflict -> {
                    throw new ResponseStatusException(CONFLICT, "Email already in use");
                });
                existing.setEmail(email);
            }
        }

        if (request.getFullName() != null) existing.setFullName(request.getFullName());
        if (request.getPhone() != null) existing.setPhone(request.getPhone());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());
        // entity has no updatedAt field; leave createdAt unchanged

        Customer updated = customerRepository.save(existing);
        return toResponse(updated);
    }

    @Override
    public void deleteCustomer(Integer id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));
        customerRepository.delete(c);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Integer id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));
        return toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            Page<Customer> page = customerRepository.findAll(pageable);
            return page.map(this::toResponse);
        }
        String term = q.trim().toLowerCase();
        List<CustomerResponse> filtered = customerRepository.findAll().stream()
                .filter(c -> {
                    if (c == null) return false;
                    if (c.getFullName() != null && c.getFullName().toLowerCase().contains(term)) return true;
                    if (c.getEmail() != null && c.getEmail().toLowerCase().contains(term)) return true;
                    if (c.getPhone() != null && c.getPhone().toLowerCase().contains(term)) return true;
                    return false;
                })
                .map(this::toResponse)
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (int) pageable.getOffset();
        if (start >= filtered.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<CustomerResponse> content = filtered.subList(start, end);
        return new PageImpl<>(content, pageable, total);
    }

    // helper: convert entity -> DTO, mapping available fields and converting timestamps
    private CustomerResponse toResponse(Customer c) {
        if (c == null) return null;
        CustomerResponse r = new CustomerResponse();
        r.setId(c.getCustomerId());
        r.setFullName(c.getFullName());
        r.setEmail(c.getEmail());
        r.setPhoneNumber(c.getPhone());
        r.setAddress(c.getAddress());

        LocalDateTime created = c.getCreatedAt();
        if (created != null) {
            OffsetDateTime odt = created.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            r.setCreatedAt(odt);
        } else {
            r.setCreatedAt(null);
        }
        // updatedAt not available on entity -> leave null
        r.setUpdatedAt(null);

        // other fields in CustomerResponse (dateOfBirth, status, preferredContactChannel, vehicles)
        // are not present on entity and will remain null
        return r;
    }
}