package swp.group4.be_ev_service_center_management.service ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import swp.group4.be_ev_service_center_management.dto.request.VehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.VehicleService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Tạo mới xe cho khách hàng
     */
    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        // Kiểm tra customer có tồn tại không
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        // Tạo entity mới
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setImageUrl(request.getImageUrl());
        vehicle.setModel(request.getModel());
        vehicle.setVin(request.getVin());
        vehicle.setLicensePlate(request.getPlateNumber());
        vehicle.setCurrentMileage(request.getMileage());
        vehicle.setLastServiceDate(request.getLastServiceDate());

        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    /**
     * Cập nhật thông tin xe
     */
    @Override
    public VehicleResponse updateVehicle(Integer id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        if (request.getCustomerId() != null) {
            Customer newCustomer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            existing.setCustomer(newCustomer);
        }

        if (request.getImageUrl() != null) existing.setImageUrl(request.getImageUrl());
        if (request.getModel() != null) existing.setModel(request.getModel());
        if (request.getVin() != null) existing.setVin(request.getVin());
        if (request.getPlateNumber() != null) existing.setLicensePlate(request.getPlateNumber());
        if (request.getMileage() != null) existing.setCurrentMileage(request.getMileage());
        if (request.getLastServiceDate() != null) existing.setLastServiceDate(request.getLastServiceDate());

        Vehicle updated = vehicleRepository.save(existing);
        return toResponse(updated);
    }

    /**
     * Lấy chi tiết xe theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        return toResponse(vehicle);
    }

    /**
     * Lấy danh sách xe theo customer
     */
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByCustomer(Integer customerId) {
        List<Vehicle> list = vehicleRepository.findByCustomerId(customerId);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Tìm kiếm xe theo từ khóa
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> searchVehicles(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return vehicleRepository.findAll(pageable).map(this::toResponse);
        }

        String lower = keyword.trim().toLowerCase();
        List<Vehicle> all = vehicleRepository.findAll().stream()
                .filter(v ->
                        (v.getLicensePlate() != null && v.getLicensePlate().toLowerCase().contains(lower))
                                || (v.getModel() != null && v.getModel().toLowerCase().contains(lower))
                                || (v.getVin() != null && v.getVin().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<VehicleResponse> content = all.subList(start, end).stream().map(this::toResponse).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, all.size());
    }

    /**
     * Cập nhật số km hiện tại của xe
     */
    @Override
    public VehicleResponse updateMileage(Integer id, Integer mileage) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        if (mileage == null || mileage < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mileage");
        }

        vehicle.setCurrentMileage(mileage);
        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    /**
     * Xóa xe
     */
    @Override
    public void deleteVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        vehicleRepository.delete(vehicle);
    }

    // =================== HELPER MAPPING ===================
    private VehicleResponse toResponse(Vehicle v) {
        if (v == null) return null;
        VehicleResponse r = new VehicleResponse();
        r.setId(v.getVehicleId());
        r.setCustomerId(v.getCustomer() != null ? v.getCustomer().getCustomerId() : null);
        r.setPlateNumber(v.getLicensePlate());
        r.setVin(v.getVin());
        r.setModel(v.getModel());
        r.setMileage(v.getCurrentMileage());
        r.setImageUrl(v.getImageUrl());
        r.setLastServiceDate(v.getLastServiceDate());
        return r;
    }
}
