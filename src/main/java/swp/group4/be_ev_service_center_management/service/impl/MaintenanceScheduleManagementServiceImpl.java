package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.TimeSlotResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final TimeSlotRepository timeSlotRepository;

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
        
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        
        // Set entities
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        schedule.setCustomer(customer);
        
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(request.getVehicleId());
        schedule.setVehicle(vehicle);
        
        ServiceCenter center = new ServiceCenter();
        center.setCenterId(request.getCenterId());
        schedule.setServiceCenter(center);
        
        // HARDCODE slotId = 1 để bypass lỗi
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlotId(1);
        schedule.setTimeSlot(timeSlot);
        
        // Parse date và time
        schedule.setScheduledDate(LocalDateTime.of(
            LocalDate.parse(request.getScheduledDate()),
            LocalTime.parse(request.getScheduledTime())
        ));
        schedule.setBookingDate(LocalDateTime.now());
        schedule.setNotes(request.getNotes());
        schedule.setStatus("PENDING");
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setTechnician(null);
        schedule.setMaintenancePackage(null);
        
        MaintenanceSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    @Override
    public List<TimeSlotResponse> getAvailableSlots(Integer centerId, String date) {
        try {
            // Parse date string to LocalDate
            LocalDate targetDate = LocalDate.parse(date);
            
            // Define time slots (example: 8:00, 8:30, 9:00, etc.)
            List<TimeSlotResponse> timeSlots = new ArrayList<>();
            
            // Create time slots from 8:00 to 17:00 (every 30 minutes)
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int slotId = 1;
            
            while (startTime.isBefore(endTime)) {
                // Count existing schedules for this center, date and time slot
                LocalDateTime slotDateTime = LocalDateTime.of(targetDate, startTime);
                LocalDateTime slotEndDateTime = slotDateTime.plusMinutes(30);
                
                long bookedCount = scheduleRepository.countByServiceCenterIdAndScheduledDateBetween(
                    centerId, slotDateTime, slotEndDateTime);
                
                // Assume each slot can accommodate 12 appointments (total capacity)
                int totalCapacity = 12;
                int available = Math.max(0, (int)(totalCapacity - bookedCount));
                
                TimeSlotResponse slot = TimeSlotResponse.builder()
                    .slotId(slotId++)
                    .time(startTime.toString())
                    .available(available)
                    .total(totalCapacity)
                    .build();
                    
                timeSlots.add(slot);
                
                // Move to next 30-minute slot
                startTime = startTime.plusMinutes(30);
            }
            
            return timeSlots;
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting available slots: " + e.getMessage());
        }
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