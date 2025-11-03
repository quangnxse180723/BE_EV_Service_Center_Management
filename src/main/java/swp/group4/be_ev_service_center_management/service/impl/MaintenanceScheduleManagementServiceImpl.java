package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleDTO;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.TimeSlotResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceScheduleManagementService;
import org.springframework.scheduling.annotation.Scheduled;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;


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
    private final TimeSlotRepository timeSlotRepository;
  // thêm dependency notification + account repo
    private final NotificationService notificationService;
    private final AccountRepository accountRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ✅ THÊM METHOD NÀY
    @Override
    @Transactional
    public List<MaintenanceScheduleDTO> getSchedulesByCustomerId(Integer customerId) {
        // Lấy entities từ repository
        List<MaintenanceSchedule> schedules = scheduleRepository.findByCustomer_CustomerId(customerId);
        
        // ✅ Fix data cũ: Nếu scheduledTime null, extract từ scheduledDate
        schedules.forEach(schedule -> {
            if (schedule.getScheduledTime() == null && schedule.getScheduledDate() != null) {
                schedule.setScheduledTime(schedule.getScheduledDate().toLocalTime());
                scheduleRepository.save(schedule);
            }
        });
        
        // Map sang DTO với format scheduledTime đúng
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

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
        
        System.out.println("=== BOOKING REQUEST ===");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Vehicle ID: " + request.getVehicleId());
        System.out.println("Center ID: " + request.getCenterId());
        System.out.println("Slot ID: " + request.getSlotId());
        System.out.println("Scheduled Date: " + request.getScheduledDate());
        System.out.println("Scheduled Time: " + request.getScheduledTime());
        
        // Parse date và time
        LocalDate scheduledDate = LocalDate.parse(request.getScheduledDate());
        LocalTime scheduledTime = LocalTime.parse(request.getScheduledTime());
        
        // Tìm ServiceCenter
        ServiceCenter serviceCenter = serviceCenterRepository.findById(request.getCenterId())
            .orElseThrow(() -> new RuntimeException("Service Center not found with ID: " + request.getCenterId()));
        
        // Xử lý slotId: Nếu không có, tạo TimeSlot mới và lấy ID
        Integer finalSlotId;
        if (request.getSlotId() != null) {
            // Validate TimeSlot tồn tại
            timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Time Slot not found with ID: " + request.getSlotId()));
            finalSlotId = request.getSlotId();
            System.out.println("✅ Using existing TimeSlot ID: " + finalSlotId);
        } else {
            // Tạo TimeSlot mới
            TimeSlot newSlot = new TimeSlot();
            newSlot.setServiceCenter(serviceCenter);
            newSlot.setDate(scheduledDate);
            newSlot.setStartTime(scheduledTime);
            newSlot.setEndTime(scheduledTime.plusMinutes(30));
            newSlot.setStatus("BOOKED");
            TimeSlot savedSlot = timeSlotRepository.save(newSlot);
            finalSlotId = savedSlot.getSlotId();
            System.out.println("✅ Created new TimeSlot ID: " + finalSlotId);
        }
        
        // Tìm Customer
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        // Tìm Vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));
        
        // Tạo MaintenanceSchedule
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setCustomer(customer);
        schedule.setVehicle(vehicle);
        schedule.setServiceCenter(serviceCenter);
        
        // Dùng setSlotId thay vì setTimeSlot
        schedule.setSlotId(finalSlotId);
        
        schedule.setScheduledDate(LocalDateTime.of(scheduledDate, scheduledTime));
        schedule.setScheduledTime(scheduledTime); // ✅ Lưu giờ thuần: 16:00:00, KHÔNG convert timezone
        schedule.setBookingDate(LocalDateTime.now());
        schedule.setNotes(request.getNotes());
        schedule.setStatus("PENDING");
        schedule.setCreatedAt(LocalDateTime.now());
        
        // Set NULL cho technician và package
        schedule.setTechnician(null);
        schedule.setMaintenancePackage(null);
        
        // Save vào database
        MaintenanceSchedule saved = scheduleRepository.save(schedule);
        
        System.out.println("✅ Schedule saved with ID: " + saved.getScheduleId() + " and slot_id: " + saved.getSlotId());
        System.out.println("===================");
        
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
     * CÁCH 2: Converter method
     * Convert MaintenanceSchedule entity sang MaintenanceScheduleDTO
     */
    private MaintenanceScheduleDTO convertToDTO(MaintenanceSchedule schedule) {
        MaintenanceScheduleDTO dto = new MaintenanceScheduleDTO();
        
        // Schedule ID and Customer ID
        dto.setScheduleId(schedule.getScheduleId());
        dto.setCustomerId(schedule.getCustomer() != null ? schedule.getCustomer().getCustomerId() : null);
        
        // Vehicle
        if (schedule.getVehicle() != null) {
            dto.setVehicleId(schedule.getVehicle().getVehicleId());
            dto.setVehicleModel(schedule.getVehicle().getModel());
            dto.setVehicleLicensePlate(schedule.getVehicle().getLicensePlate());
        }
        
        // Center
        if (schedule.getServiceCenter() != null) {
            dto.setCenterId(schedule.getServiceCenter().getCenterId());
            dto.setCenterName(schedule.getServiceCenter().getName());
        }
        
        // Service
        if (schedule.getMaintenancePackage() != null) {
            dto.setServiceId(schedule.getMaintenancePackage().getPackageId());
            dto.setServiceName(schedule.getMaintenancePackage().getName());
        }
        
        // Map dates
        dto.setScheduledDate(schedule.getScheduledDate() != null 
            ? schedule.getScheduledDate().toString() 
            : null);
        
        // ✅ THÊM DÒNG NÀY - Map scheduledTime
        dto.setScheduledTime(schedule.getScheduledTime() != null 
            ? schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            : null);
        
        dto.setStatus(schedule.getStatus());
        dto.setNotes(schedule.getNotes());
        
        return dto;
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
   // Thêm phương thức scheduled tại đây (không tạo package mới)
    // Chạy mỗi ngày lúc 08:00 - tạo notification UPCOMING và OVERDUE
    @Scheduled(cron = "0 0 8 * * *")
    public void generateUpcomingAndOverdueNotifications() {
        LocalDateTime now = LocalDateTime.now();

        // phạm vi UPCOMING: trong 1..3 ngày tới
        LocalDateTime from = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime to = now.plusDays(3).withHour(23).withMinute(59).withSecond(59);

        // Lấy schedules bằng query phù hợp nếu có; ở đây dùng findAll() đơn giản
        List<MaintenanceSchedule> schedules = scheduleRepository.findAll();

        for (MaintenanceSchedule s : schedules) {
            if (s == null || s.getScheduledDate() == null || s.getCustomer() == null || s.getCustomer().getAccount() == null) continue;

            var receiver = s.getCustomer().getAccount();
            String uniquePart = s.getScheduledDate().toLocalDate().toString() + "|" + (s.getVehicle() != null ? s.getVehicle().getLicensePlate() : "");

            // UPCOMING
            if ((s.getScheduledDate().isEqual(from) || s.getScheduledDate().isAfter(from)) &&
                    (s.getScheduledDate().isEqual(to) || s.getScheduledDate().isBefore(to)) &&
                    ("PENDING".equalsIgnoreCase(s.getStatus()) || "CONFIRMED".equalsIgnoreCase(s.getStatus()))) {

                boolean exists = notificationService.getNotificationsForAccount(receiver.getEmail(), 0, 200)
                        .stream()
                        .anyMatch(n -> "UPCOMING".equalsIgnoreCase(n.getType()) && n.getMessage() != null && n.getMessage().contains(uniquePart));

                if (!exists) {
                    var sender = accountRepository.findByEmail("system@local").orElse(null);
                    String title = "Sắp đến hạn bảo dưỡng";
                    String message = "Xe " + (s.getVehicle() != null ? s.getVehicle().getModel() + " (" + s.getVehicle().getLicensePlate() + ")" : "xe của bạn")
                            + " sắp đến hạn bảo dưỡng vào ngày " + s.getScheduledDate().toLocalDate() + ". (" + uniquePart + ")";

                    notificationService.createNotification(sender, receiver, null, "UPCOMING", title, message);
                }
            }

            // OVERDUE
            if (s.getScheduledDate().isBefore(now) && !"COMPLETED".equalsIgnoreCase(s.getStatus()) && !"DONE".equalsIgnoreCase(s.getStatus())) {
                boolean exists = notificationService.getNotificationsForAccount(receiver.getEmail(), 0, 200)
                        .stream()
                        .anyMatch(n -> "OVERDUE".equalsIgnoreCase(n.getType()) && n.getMessage() != null && n.getMessage().contains(uniquePart));

                if (!exists) {
                    var sender = accountRepository.findByEmail("system@local").orElse(null);
                    String title = "Quá hạn bảo dưỡng";
                    String message = "Xe " + (s.getVehicle() != null ? s.getVehicle().getModel() + " (" + s.getVehicle().getLicensePlate() + ")" : "xe của bạn")
                            + " đã quá hạn bảo dưỡng từ ngày " + s.getScheduledDate().toLocalDate() + ". (" + uniquePart + ")";

                    notificationService.createNotification(sender, receiver, null, "OVERDUE", title, message);
                }
            }
        }
    } 
}