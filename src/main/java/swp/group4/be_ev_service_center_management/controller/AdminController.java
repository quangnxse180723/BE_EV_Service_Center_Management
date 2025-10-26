package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;
    private final VehicleRepository vehicleRepository;
    //private final InvoiceRepository invoiceRepository;
   // private final PartRepository partRepository;
    

    // ----------- Quản lý nhân viên -----------
     @GetMapping("/staffs")
    public List<Staff> getAllStaffs() {
        return staffRepository.findAll();
    }

    @PostMapping("/staffs")
    public Staff createStaff(@RequestBody Staff staff) {
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
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable Integer id, @RequestBody Customer customer) {
        customer.setCustomerId(id);
        return customerRepository.save(customer);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Integer id) {
        customerRepository.deleteById(id);
    }

    // ----------- Quản lý kỹ thuật viên -----------
    @GetMapping("/technicians")
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @PostMapping("/technicians")
    public Technician createTechnician(@RequestBody Technician technician) {
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

    // ----------- Quản lý doanh thu -----------
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