package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceScheduleManagementServiceImpl implements MaintenanceScheduleManagementService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final TechnicianRepository technicianRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final MaintenancePackageRepository maintenancePackageRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<MaintenanceScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceScheduleResponse getScheduleById(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
        return toResponse(schedule);
    }

    @Override
    @Transactional
    public MaintenanceScheduleResponse updateScheduleStatus(Integer scheduleId, UpdateMaintenanceScheduleRequest request) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));

        // Cập nhật trạng thái
        schedule.setStatus(request.getStatus());

        // ✅ Gán kỹ thuật viên nếu có technicianId
        if (request.getTechnicianId() != null) {
            Technician technician = technicianRepository.findById(request.getTechnicianId())
                    .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + request.getTechnicianId()));
            schedule.setTechnician(technician);
        }

        MaintenanceSchedule updatedSchedule = scheduleRepository.save(schedule);

        return toResponse(updatedSchedule);
    }

    @Override
    @Transactional
    public MaintenanceScheduleResponse assignTechnician(Integer scheduleId, AssignTechnicianRequest request) {
        // Tìm lịch hẹn
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
        
        // Tìm kỹ thuật viên
        Technician technician = technicianRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + request.getTechnicianId()));
        
        // Gán kỹ thuật viên
        schedule.setTechnician(technician);
        
        // Lưu lại
        MaintenanceSchedule updatedSchedule = scheduleRepository.save(schedule);
        
        return toResponse(updatedSchedule);
    }

    @Override
    public List<MaintenanceScheduleResponse> searchByCustomerName(String name) {
        return scheduleRepository.findByCustomerNameContaining(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceScheduleResponse> searchByLicensePlate(String plate) {
        return scheduleRepository.findByLicensePlateContaining(plate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceScheduleResponse> searchByStatus(String status) {
        return scheduleRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaintenanceScheduleResponse bookSchedule(BookScheduleRequest request, Integer customerId) {
        // Tìm customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Tìm vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));

        // Kiểm tra vehicle có thuộc về customer không
        if (!vehicle.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Vehicle does not belong to this customer");
        }

        // Tìm service center
        ServiceCenter serviceCenter = serviceCenterRepository.findById(request.getCenterId())
                .orElseThrow(() -> new RuntimeException("Service Center not found with ID: " + request.getCenterId()));

        // Tạo schedule mới
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setCustomer(customer);
        schedule.setVehicle(vehicle);
        schedule.setServiceCenter(serviceCenter);
        schedule.setScheduledDate(request.getScheduledDate());
        schedule.setBookingDate(LocalDateTime.now());
        schedule.setStatus("PENDING"); // Trạng thái chờ xác nhận
        schedule.setNotes(request.getNotes());

        // Gán maintenance package nếu có
        if (request.getPackageId() != null) {
            MaintenancePackage maintenancePackage = maintenancePackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Maintenance Package not found with ID: " + request.getPackageId()));
            schedule.setMaintenancePackage(maintenancePackage);
        }

        // Lưu schedule
        MaintenanceSchedule savedSchedule = scheduleRepository.save(schedule);

        return toResponse(savedSchedule);
    }

    /**
     * Helper method: Map MaintenanceSchedule entity sang MaintenanceScheduleResponse DTO
     */
    private MaintenanceScheduleResponse toResponse(MaintenanceSchedule schedule) {
        return MaintenanceScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .scheduledDate(schedule.getScheduledDate() != null ? schedule.getScheduledDate().format(FORMATTER) : null)
                .status(schedule.getStatus())
                .customerId(schedule.getCustomer().getCustomerId())
                .customerName(schedule.getCustomer().getFullName())
                .vehicleId(schedule.getVehicle().getVehicleId())
                .vehicleModel(schedule.getVehicle().getModel())
                .licensePlate(schedule.getVehicle().getLicensePlate())
                // Thêm thông tin technician
                .technicianId(schedule.getTechnician() != null ? schedule.getTechnician().getTechnicianId() : null)
                .technicianName(schedule.getTechnician() != null ? schedule.getTechnician().getFullName() : null)
                .build();
    }
}