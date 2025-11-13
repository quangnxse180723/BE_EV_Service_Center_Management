package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
// ...existing code...
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.stream.Collectors;
// ...existing code...
import java.util.List;
import swp.group4.be_ev_service_center_management.service.impl.RevenueService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;
    private final VehicleRepository vehicleRepository;
    //private final InvoiceRepository invoiceRepository;
    private final PartRepository partRepository;
    private final RevenueService revenueService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    

    // ----------- Quản lý nhân viên -----------
     @GetMapping("/staffs")
    public List<Staff> getAllStaffs() {
        return staffRepository.findAll();
    }

    @PostMapping("/staffs")
    public Staff createStaff(@RequestBody Map<String, Object> requestBody) {
        // Lấy thông tin từ request
        String password = (String) requestBody.get("password");
        String email = (String) requestBody.get("email");
        String fullName = (String) requestBody.get("fullName");
        String phone = (String) requestBody.get("phone");
        
        // Tạo Account trước
        Account account = new Account();
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setRole("STAFF");
        account.setIsActive(true);
        Account savedAccount = accountRepository.save(account);
        
        // Tạo Staff với Account vừa tạo
        Staff staff = new Staff();
        staff.setAccount(savedAccount);
        staff.setFullName(fullName);
        staff.setEmail(email);
        staff.setPhone(phone);
        // Set serviceCenter mặc định (ID = 1)
        ServiceCenter defaultCenter = new ServiceCenter();
        defaultCenter.setCenterId(1);
        staff.setServiceCenter(defaultCenter);
        
        return staffRepository.save(staff);
    }

    @PutMapping("/staffs/{id}")
    public Staff updateStaff(@PathVariable Integer id, @RequestBody Staff staff) {
        staff.setStaffId(id);
        return staffRepository.save(staff);
    }

    @DeleteMapping("/staffs/{id}")
    public void deleteStaff(@PathVariable Integer id) {
        staffRepository.deleteById(id);
    }
 
    // ----------- Quản lý khách hàng -----------
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Map<String, Object> requestBody) {
        // Lấy thông tin từ request
        String password = (String) requestBody.get("password");
        String email = (String) requestBody.get("email");
        String fullName = (String) requestBody.get("fullName");
        String phone = (String) requestBody.get("phone");
        String address = (String) requestBody.get("address");
        
        // Tạo Account trước
        Account account = new Account();
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setRole("CUSTOMER");
        account.setIsActive(true);
        Account savedAccount = accountRepository.save(account);
        
        // Tạo Customer với Account vừa tạo
        Customer customer = new Customer();
        customer.setAccount(savedAccount);
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        
        return customerRepository.save(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable Integer id, @RequestBody Customer customerUpdate) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Chỉ update các field được gửi lên, giữ nguyên account
        if (customerUpdate.getFullName() != null) {
            existing.setFullName(customerUpdate.getFullName());
        }
        if (customerUpdate.getEmail() != null) {
            existing.setEmail(customerUpdate.getEmail());
        }
        if (customerUpdate.getPhone() != null) {
            existing.setPhone(customerUpdate.getPhone());
        }
        if (customerUpdate.getAddress() != null) {
            existing.setAddress(customerUpdate.getAddress());
        }
        
        return customerRepository.save(existing);
    }

    // Khóa/Mở khóa tài khoản customer
    @PutMapping("/customers/{id}/toggle-lock")
    public Customer toggleCustomerLock(@PathVariable Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        Account account = customer.getAccount();
        if (account != null) {
            // Toggle trạng thái: true -> false hoặc false -> true
            account.setIsActive(!account.getIsActive());
            accountRepository.save(account);
        }
        
        return customer;
    }

    // ----------- Quản lý kỹ thuật viên -----------
    @GetMapping("/technicians")
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @PostMapping("/technicians")
    public Technician createTechnician(@RequestBody Map<String, Object> requestBody) {
        // Lấy thông tin từ request
        String password = (String) requestBody.get("password");
        String email = (String) requestBody.get("email");
        String fullName = (String) requestBody.get("fullName");
        String phone = (String) requestBody.get("phone");
        
        // Tạo Account trước
        Account account = new Account();
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setRole("TECHNICIAN");
        account.setIsActive(true);
        Account savedAccount = accountRepository.save(account);
        
        // Tạo Technician với Account vừa tạo
        Technician technician = new Technician();
        technician.setAccount(savedAccount);
        technician.setFullName(fullName);
        technician.setEmail(email);
        technician.setPhone(phone);
        // Set serviceCenter mặc định (ID = 1)
        ServiceCenter defaultCenter = new ServiceCenter();
        defaultCenter.setCenterId(1);
        technician.setServiceCenter(defaultCenter);
        
        return technicianRepository.save(technician);
    }

    @PutMapping("/technicians/{id}")
    public Technician updateTechnician(@PathVariable Integer id, @RequestBody Technician technician) {
        technician.setTechnicianId(id);
        return technicianRepository.save(technician);
    }

    @DeleteMapping("/technicians/{id}")
    public void deleteTechnician(@PathVariable Integer id) {
        technicianRepository.deleteById(id);
    }

    // ----------- Quản lý hóa đơn -----------
   /*  @GetMapping("/invoices")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @PostMapping("/invoices")
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @PutMapping("/invoices/{id}")
    public Invoice updateInvoice(@PathVariable Integer id, @RequestBody Invoice invoice) {
        invoice.setInvoiceId(id);
        return invoiceRepository.save(invoice);
    }

    @DeleteMapping("/invoices/{id}")
    public void deleteInvoice(@PathVariable Integer id) {
        invoiceRepository.deleteById(id);
    }

     ----------- Quản lý doanh thu -----------
    @GetMapping("/revenue/summary")
    public RevenueSummary getRevenueSummary() {
        List<Invoice> invoices = invoiceRepository.findAll();
        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalCost = invoices.stream().mapToDouble(Invoice::getTotalCost).sum();
        double totalProfit = totalRevenue - totalCost;
        return new RevenueSummary(totalRevenue, totalCost, totalProfit);
    }

    public static class RevenueSummary {
        public double totalRevenue;
        public double totalCost;
        public double totalProfit;

        public RevenueSummary(double totalRevenue, double totalCost, double totalProfit) {
            this.totalRevenue = totalRevenue;
            this.totalCost = totalCost;
            this.totalProfit = totalProfit;
        }
    }

    // ----------- Quản lý phụ tùng -----------
    @GetMapping("/parts")
    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    @PostMapping("/parts")
    public Part createPart(@RequestBody Part part) {
        return partRepository.save(part);
    }

    @PutMapping("/parts/{id}")
    public Part updatePart(@PathVariable Integer id, @RequestBody Part part) {
        part.setPartId(id);
        return partRepository.save(part);
    }

    @DeleteMapping("/parts/{id}")
    public void deletePart(@PathVariable Integer id) {
        partRepository.deleteById(id);
    }
 */
// Parts CRUD (uncommented + safe)
    @GetMapping("/parts")
    public List<Part> getAllParts() { return partRepository.findAll(); }

    @PostMapping("/parts")
    public Part createPart(@RequestBody Part part) {
        // Ensure quantity fields are not null
        if (part.getQuantityInStock() == null) part.setQuantityInStock(0);
        if (part.getMinStock() == null) part.setMinStock(0);
        return partRepository.save(part);
    }

    @PutMapping("/parts/{id}")
    public Part updatePart(@PathVariable Integer id, @RequestBody Part part) {
        return partRepository.findById(id).map(existing -> {
            if (part.getName() != null) existing.setName(part.getName());
            if (part.getPrice() != null) existing.setPrice(part.getPrice());
            if (part.getQuantityInStock() != null) existing.setQuantityInStock(part.getQuantityInStock());
            if (part.getMinStock() != null) existing.setMinStock(part.getMinStock());
            return partRepository.save(existing);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found"));
    }

    @DeleteMapping("/parts/{id}")
    public void deletePart(@PathVariable Integer id) { partRepository.deleteById(id); }

    // Revenue endpoints
    @GetMapping("/revenue/summary")
    public swp.group4.be_ev_service_center_management.dto.response.RevenueSummary getRevenueSummary(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate f = (from == null || from.isBlank()) ? LocalDate.now().minusMonths(1) : LocalDate.parse(from);
        LocalDate t = (to == null || to.isBlank()) ? LocalDate.now() : LocalDate.parse(to);
        return revenueService.getSummary(f, t);
    }

    @GetMapping("/revenue/groups")
    public java.util.List<swp.group4.be_ev_service_center_management.dto.response.RevenueGroupDTO> revenueGroups(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "day") String groupBy) {

        LocalDate f = (from == null || from.isBlank()) ? LocalDate.now().minusMonths(1) : LocalDate.parse(from);
        LocalDate t = (to == null || to.isBlank()) ? LocalDate.now() : LocalDate.parse(to);
        return revenueService.getGrouped(f, t, groupBy);
    }
// ...existing code...
    // ----------- Quản lý xe -----------
    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @PostMapping("/vehicles")
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @PutMapping("/vehicles/{id}")
    public Vehicle updateVehicle(@PathVariable Integer id, @RequestBody Vehicle vehicle) {
        vehicle.setVehicleId(id);
        return vehicleRepository.save(vehicle);
    }

    @DeleteMapping("/vehicles/{id}")
    public void deleteVehicle(@PathVariable Integer id) {
        vehicleRepository.deleteById(id);
    }
}