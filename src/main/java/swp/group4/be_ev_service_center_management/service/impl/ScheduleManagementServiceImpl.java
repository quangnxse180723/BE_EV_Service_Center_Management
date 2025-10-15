package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.CreateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.ScheduleFilterRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.ScheduleManagementService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleManagementServiceImpl implements ScheduleManagementService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final MaintenanceRecordRepository recordRepository;

    @Override
    @Transactional
    public ScheduleResponse createSchedule(Integer staffId, CreateScheduleRequest request) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        // Validate vehicle belongs to customer
        if (!vehicle.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Vehicle does not belong to this customer");
        }
        
        // Validate time slot
        TimeSlot timeSlot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        
        // Validate time slot belongs to same service center
        if (!timeSlot.getServiceCenter().getCenterId().equals(staff.getServiceCenter().getCenterId())) {
            throw new RuntimeException("Time slot must be from the same service center");
        }
        
        // Validate scheduled date is in future
        if (request.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Scheduled date must be in the future");
        }
        
        // Create schedule
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setCustomer(customer);
        schedule.setVehicle(vehicle);
        schedule.setServiceCenter(staff.getServiceCenter());
        schedule.setTimeSlot(timeSlot);
        schedule.setBookingDate(LocalDateTime.now());
        schedule.setScheduledDate(request.getScheduledDate());
        schedule.setStatus("PENDING");
        
        // Set package if provided
        if (request.getPackageId() != null) {
            // TODO: Validate and set package
        }
        
        schedule = scheduleRepository.save(schedule);
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(Integer staffId, UpdateScheduleRequest request) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Validate schedule
        MaintenanceSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        // Validate schedule belongs to same service center
        if (!schedule.getServiceCenter().getCenterId().equals(staff.getServiceCenter().getCenterId())) {
            throw new RuntimeException("Schedule must be from the same service center");
        }
        
        // Can only update if status is PENDING or CONFIRMED
        if (!"PENDING".equals(schedule.getStatus()) && !"CONFIRMED".equals(schedule.getStatus())) {
            throw new RuntimeException("Can only update PENDING or CONFIRMED schedules");
        }
        
        // Update fields
        if (request.getScheduledDate() != null) {
            if (request.getScheduledDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Scheduled date must be in the future");
            }
            schedule.setScheduledDate(request.getScheduledDate());
        }
        
        if (request.getSlotId() != null) {
            TimeSlot timeSlot = timeSlotRepository.findById(request.getSlotId())
                    .orElseThrow(() -> new RuntimeException("Time slot not found"));
            schedule.setTimeSlot(timeSlot);
        }
        
        schedule = scheduleRepository.save(schedule);
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponse confirmSchedule(Integer staffId, Integer scheduleId) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Validate schedule
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        // Validate schedule belongs to same service center
        if (!schedule.getServiceCenter().getCenterId().equals(staff.getServiceCenter().getCenterId())) {
            throw new RuntimeException("Schedule must be from the same service center");
        }
        
        // Can only confirm if status is PENDING
        if (!"PENDING".equals(schedule.getStatus())) {
            throw new RuntimeException("Can only confirm PENDING schedules");
        }
        
        schedule.setStatus("CONFIRMED");
        schedule = scheduleRepository.save(schedule);
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponse cancelSchedule(Integer staffId, Integer scheduleId, String reason) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Validate schedule
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        // Validate schedule belongs to same service center
        if (!schedule.getServiceCenter().getCenterId().equals(staff.getServiceCenter().getCenterId())) {
            throw new RuntimeException("Schedule must be from the same service center");
        }
        
        // Cannot cancel if already IN_PROGRESS or DONE
        if ("IN_PROGRESS".equals(schedule.getStatus()) || "DONE".equals(schedule.getStatus())) {
            throw new RuntimeException("Cannot cancel IN_PROGRESS or DONE schedules");
        }
        
        schedule.setStatus("CANCELLED");
        schedule = scheduleRepository.save(schedule);
        
        // TODO: Create notification for customer about cancellation
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    public ScheduleDetailResponse getScheduleDetailById(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        return mapToScheduleDetailResponse(schedule);
    }

    @Override
    public List<ScheduleResponse> searchSchedules(Integer staffId, ScheduleFilterRequest filter) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        
        // Parse dates if provided
        LocalDateTime dateFrom = null;
        LocalDateTime dateTo = null;
        
        if (filter.getDateFrom() != null && !filter.getDateFrom().isEmpty()) {
            dateFrom = LocalDateTime.parse(filter.getDateFrom() + "T00:00:00");
        }
        
        if (filter.getDateTo() != null && !filter.getDateTo().isEmpty()) {
            dateTo = LocalDateTime.parse(filter.getDateTo() + "T23:59:59");
        }
        
        // Search with filters
        List<MaintenanceSchedule> schedules = scheduleRepository.searchSchedules(
                centerId,
                filter.getStatus(),
                filter.getCustomerName(),
                filter.getVehiclePlate(),
                filter.getPackageId(),
                dateFrom,
                dateTo
        );
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getSchedulesByDateRange(Integer staffId, LocalDateTime from, LocalDateTime to) {
        // Validate staff
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        
        List<MaintenanceSchedule> schedules = scheduleRepository
                .findByServiceCenter_CenterIdAndScheduledDateBetween(centerId, from, to);
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getWeeklySchedules(Integer staffId, LocalDateTime weekStart) {
        LocalDateTime weekEnd = weekStart.plusDays(7).with(LocalTime.MAX);
        return getSchedulesByDateRange(staffId, weekStart, weekEnd);
    }

    @Override
    public List<ScheduleResponse> getMonthlySchedules(Integer staffId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        
        return getSchedulesByDateRange(staffId, monthStart, monthEnd);
    }

    @Override
    public List<ScheduleResponse> getCustomerScheduleHistory(Integer customerId) {
        List<MaintenanceSchedule> schedules = scheduleRepository.findByCustomer_CustomerId(customerId);
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getVehicleScheduleHistory(Integer vehicleId) {
        List<MaintenanceSchedule> schedules = scheduleRepository.findByVehicle_VehicleId(vehicleId);
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private ScheduleResponse mapToScheduleResponse(MaintenanceSchedule schedule) {
        ScheduleResponse.ScheduleResponseBuilder builder = ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .scheduledDate(schedule.getScheduledDate())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt());
        
        // Vehicle info
        if (schedule.getVehicle() != null) {
            builder.vehicleId(schedule.getVehicle().getVehicleId())
                   .vehiclePlateNumber(schedule.getVehicle().getLicensePlate())
                   .vehicleModel(schedule.getVehicle().getModel());
        }
        
        // Customer info
        if (schedule.getCustomer() != null) {
            builder.customerId(schedule.getCustomer().getCustomerId())
                   .customerName(schedule.getCustomer().getFullName())
                   .customerPhone(schedule.getCustomer().getPhone());
        }
        
        // Time slot info
        if (schedule.getTimeSlot() != null) {
            builder.timeSlot(schedule.getTimeSlot().getStartTime() + " - " + schedule.getTimeSlot().getEndTime());
        }
        
        // Package info
        if (schedule.getMaintenancePackage() != null) {
            builder.packageName(schedule.getMaintenancePackage().getName());
        }
        
        // Technician info
        recordRepository.findByMaintenanceSchedule_ScheduleId(schedule.getScheduleId())
                .ifPresent(record -> {
                    if (record.getTechnician() != null) {
                        builder.technicianId(record.getTechnician().getTechnicianId())
                               .technicianName(record.getTechnician().getFullName());
                    }
                });
        
        return builder.build();
    }

    private ScheduleDetailResponse mapToScheduleDetailResponse(MaintenanceSchedule schedule) {
        ScheduleDetailResponse.ScheduleDetailResponseBuilder builder = ScheduleDetailResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .bookingDate(schedule.getBookingDate())
                .scheduledDate(schedule.getScheduledDate())
                .status(schedule.getStatus());
        
        // Customer info
        if (schedule.getCustomer() != null) {
            Customer customer = schedule.getCustomer();
            builder.customerId(customer.getCustomerId())
                   .customerName(customer.getFullName())
                   .customerPhone(customer.getPhone())
                   .customerEmail(customer.getEmail());
        }
        
        // Vehicle info
        if (schedule.getVehicle() != null) {
            Vehicle vehicle = schedule.getVehicle();
            builder.vehicleId(vehicle.getVehicleId())
                   .vehiclePlate(vehicle.getLicensePlate())
                   .vehicleModel(vehicle.getModel())
                   .vehicleVin(vehicle.getVin())
                   .vehicleMileage(vehicle.getCurrentMileage());
        }
        
        // Time slot info
        if (schedule.getTimeSlot() != null) {
            TimeSlot slot = schedule.getTimeSlot();
            builder.slotId(slot.getSlotId())
                   .timeSlotStart(slot.getStartTime().toString())
                   .timeSlotEnd(slot.getEndTime().toString());
        }
        
        // Package info
        if (schedule.getMaintenancePackage() != null) {
            MaintenancePackage pkg = schedule.getMaintenancePackage();
            builder.packageId(pkg.getPackageId())
                   .packageName(pkg.getName())
                   .packageDescription(pkg.getDescription());
        }
        
        // Service center info
        if (schedule.getServiceCenter() != null) {
            ServiceCenter center = schedule.getServiceCenter();
            builder.centerId(center.getCenterId())
                   .centerName(center.getName());
        }
        
        // Technician info
        recordRepository.findByMaintenanceSchedule_ScheduleId(schedule.getScheduleId())
                .ifPresent(record -> {
                    if (record.getTechnician() != null) {
                        builder.technicianId(record.getTechnician().getTechnicianId())
                               .technicianName(record.getTechnician().getFullName());
                    }
                });
        
        return builder.build();
    }
}
