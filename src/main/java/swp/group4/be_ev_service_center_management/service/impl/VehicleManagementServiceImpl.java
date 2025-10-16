package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.CreateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.VehicleResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.VehicleManagementService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleManagementServiceImpl implements VehicleManagementService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return toResponse(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setImageUrl(request.getImageUrl());
        vehicle.setModel(request.getModel());
        vehicle.setVin(request.getVin());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCurrentMileage(request.getCurrentMileage());
        if (request.getLastServiceDate() != null) {
            vehicle.setLastServiceDate(LocalDate.parse(request.getLastServiceDate()));
        }
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Integer id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (request.getImageUrl() != null) vehicle.setImageUrl(request.getImageUrl());
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getVin() != null) vehicle.setVin(request.getVin());
        if (request.getLicensePlate() != null) vehicle.setLicensePlate(request.getLicensePlate());
        if (request.getCurrentMileage() != null) vehicle.setCurrentMileage(request.getCurrentMileage());
        if (request.getLastServiceDate() != null) vehicle.setLastServiceDate(LocalDate.parse(request.getLastServiceDate()));
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public List<VehicleResponse> searchVehicles(String keyword) {
        return vehicleRepository
                .findByModelContainingIgnoreCaseOrLicensePlateContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> searchVehiclesByCustomerName(String customerName) {
        return vehicleRepository.findByCustomerNameContaining(customerName).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .model(vehicle.getModel())
                .vin(vehicle.getVin())
                .licensePlate(vehicle.getLicensePlate())
                .imageUrl(vehicle.getImageUrl())
                .currentMileage(vehicle.getCurrentMileage())
                .lastServiceDate(vehicle.getLastServiceDate() != null ? vehicle.getLastServiceDate().toString() : null)
                .customerId(vehicle.getCustomer().getCustomerId())
                .customerName(vehicle.getCustomer().getFullName())
                .build();
    }
}