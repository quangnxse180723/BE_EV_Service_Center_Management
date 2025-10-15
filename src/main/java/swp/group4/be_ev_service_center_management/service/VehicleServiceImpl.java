package swp.group4.be_ev_service_center_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.VehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.exception.ResourceNotFoundException;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.VehicleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepo;
    private final CustomerRepository customerRepo;

    @Override
    public VehicleResponse createVehicle(VehicleRequest req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Vehicle v = new Vehicle();
        v.setCustomer(customer);
        v.setModel(req.getModel());
        v.setVin(req.getVin());
        v.setLicensePlate(req.getLicensePlate());
        v.setCurrentMileage(req.getCurrentMileage());
        Vehicle saved = vehicleRepo.save(v);
        return entityToResponse(saved);
    }

    @Override
    public VehicleResponse updateVehicle(Integer id, VehicleRequest req) {
        Vehicle v = vehicleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (req.getCustomerId() != null) {
            Customer customer = customerRepo.findById(req.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            v.setCustomer(customer);
        }
        v.setModel(req.getModel());
        v.setVin(req.getVin());
        v.setLicensePlate(req.getLicensePlate());
        v.setCurrentMileage(req.getCurrentMileage());
        return entityToResponse(vehicleRepo.save(v));
    }

    @Override
    public void deleteVehicle(Integer id) {
        vehicleRepo.deleteById(id);
    }

    @Override
    public VehicleResponse getVehicle(Integer id) {
        return entityToResponse(vehicleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")));
    }

    @Override
    public List<VehicleResponse> getVehiclesByCustomer(Integer customerId) {
        return vehicleRepo.findByCustomerCustomerId(customerId)
                .stream().map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepo.findAll()
                .stream().map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    private VehicleResponse entityToResponse(Vehicle v) {
        VehicleResponse res = new VehicleResponse();
        res.setVehicleId(v.getVehicleId());
        //res.setCustomerId(v.getCustomer().getCustomerId());
        res.setModel(v.getModel());
        res.setVin(v.getVin());
        res.setLicensePlate(v.getLicensePlate());
        res.setCurrentMileage(v.getCurrentMileage());
        return res;
    }
}