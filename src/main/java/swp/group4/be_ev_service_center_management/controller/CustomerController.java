package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.CustomerRequest;
import swp.group4.be_ev_service_center_management.dto.response.CustomerResponse;
import swp.group4.be_ev_service_center_management.service.CustomerService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody CustomerRequest request, BindingResult br) {
        if (br.hasErrors()) {
            List<String> errors = br.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        CustomerResponse created = customerService.register(request);
        if (created != null && created.getId() != null) {
            URI location = URI.create("/api/customers/" + created.getId());
            return ResponseEntity.created(location).body(created);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> login(@RequestParam("email") String email,
                                                  @RequestParam("password") String password) {
        CustomerResponse resp = customerService.login(email, password);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Integer id) {
        CustomerResponse resp = customerService.getCustomerById(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list(@RequestParam(value = "q", required = false) String q,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        Page<CustomerResponse> page = customerService.searchCustomers(q, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Partial update allowed: do not enforce @Valid to permit updating subset of fields.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Integer id,
                                                           @RequestBody CustomerRequest request) {
        CustomerResponse updated = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}