package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.*;
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
    private final TimeSlotRepository timeSlotRepository;
    private final MaintenancePackageRepository packageRepository;
    private final InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<AppointmentResponse> getAppointments(String keyword) {
        List<MaintenanceSchedule> schedules = scheduleRepository.searchAppointments(keyword);
        return schedules.stream()
                .map(this::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MaintenanceScheduleDTO> getSchedulesByCustomerId(Integer customerId) {
        List<MaintenanceSchedule> schedules = scheduleRepository.findByCustomer_CustomerId(customerId);
        schedules.forEach(schedule -> {
            if (schedule.getScheduledTime() == null && schedule.getScheduledDate() != null) {
                schedule.setScheduledTime(schedule.getScheduledDate().toLocalTime());
                scheduleRepository.save(schedule);
            }
        });
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
        schedule.setStatus(request.getStatus());
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
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
        Technician technician = technicianRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + request.getTechnicianId()));
        schedule.setTechnician(technician);
        MaintenanceSchedule updatedSchedule = scheduleRepository.save(schedule);
        return toResponse(updatedSchedule);
    }

    @Override
    public List<MaintenanceScheduleResponse> searchByCustomerName(String name) {
        return scheduleRepository.findByCustomer_FullNameContaining(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceScheduleResponse> searchByLicensePlate(String plate) {
        return scheduleRepository.findByVehicle_LicensePlateContaining(plate).stream()
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
        LocalDate scheduledDate = LocalDate.parse(request.getScheduledDate());
        LocalTime scheduledTime = LocalTime.parse(request.getScheduledTime());
        ServiceCenter serviceCenter = serviceCenterRepository.findById(request.getCenterId())
                .orElseThrow(() -> new RuntimeException("Service Center not found with ID: " + request.getCenterId()));

        // Logic for TimeSlot remains the same
        Integer finalSlotId;
        if (request.getSlotId() != null) {
            timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Time Slot not found with ID: " + request.getSlotId()));
            finalSlotId = request.getSlotId();
        } else {
            TimeSlot newSlot = new TimeSlot();
            newSlot.setServiceCenter(serviceCenter);
            newSlot.setDate(scheduledDate);
            newSlot.setStartTime(scheduledTime);
            newSlot.setEndTime(scheduledTime.plusMinutes(30));
            newSlot.setStatus("BOOKED");
            TimeSlot savedSlot = timeSlotRepository.save(newSlot);
            finalSlotId = savedSlot.getSlotId();
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));

        // Find MaintenancePackage based on serviceId from the request
        MaintenancePackage maintenancePackage = null;
        if (request.getServiceId() != null && request.getServiceId() > 0) {
            maintenancePackage = packageRepository.findById(request.getServiceId())
                    .orElse(null); // If not found, it remains null (for ad-hoc repairs)
        }

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setCustomer(customer);
        schedule.setVehicle(vehicle);
        schedule.setServiceCenter(serviceCenter);
        schedule.setSlotId(finalSlotId);
        schedule.setScheduledDate(LocalDateTime.of(scheduledDate, scheduledTime));
        schedule.setScheduledTime(scheduledTime);
        schedule.setBookingDate(LocalDateTime.now());
        schedule.setNotes(request.getNotes());
        schedule.setStatus("PENDING");
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setTechnician(null);
        schedule.setMaintenancePackage(maintenancePackage); // Set the found package

        MaintenanceSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    @Override
    public List<TimeSlotResponse> getAvailableSlots(Integer centerId, String date) {
        try {
            LocalDate targetDate = LocalDate.parse(date);
            List<TimeSlotResponse> timeSlots = new ArrayList<>();
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(17, 0);
            int slotId = 1;
            while (startTime.isBefore(endTime)) {
                LocalDateTime slotDateTime = LocalDateTime.of(targetDate, startTime);
                LocalDateTime slotEndDateTime = slotDateTime.plusMinutes(30);
                long bookedCount = scheduleRepository.countByServiceCenterIdAndScheduledDateBetween(
                        centerId, slotDateTime, slotEndDateTime);
                int totalCapacity = 12;
                int available = Math.max(0, (int) (totalCapacity - bookedCount));
                TimeSlotResponse slot = TimeSlotResponse.builder()
                        .slotId(slotId++)
                        .time(startTime.toString())
                        .available(available)
                        .total(totalCapacity)
                        .build();
                timeSlots.add(slot);
                startTime = startTime.plusMinutes(30);
            }
            return timeSlots;
        } catch (Exception e) {
            throw new RuntimeException("Error getting available slots: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentManagementResponse> getPaymentList() {
        List<String> statuses = List.of("CHỜ_THANH_TOÁN", "ĐÃ_THANH_TOÁN");
        List<MaintenanceSchedule> schedules = scheduleRepository.findForPayment(statuses);
        return schedules.stream().map(ms -> PaymentManagementResponse.builder()
                .customerName(ms.getCustomer().getFullName())
                .vehicleName(ms.getVehicle().getModel())
                .licensePlate(ms.getVehicle().getLicensePlate())
                .scheduledDate(ms.getScheduledDate().format(DateTimeFormatter.ofPattern("HH:mm")))
                .status(mapStatus(ms.getStatus()))
                .action(mapAction(ms.getStatus()))
                .build()
        ).collect(Collectors.toList());
    }

    private MaintenanceScheduleDTO convertToDTO(MaintenanceSchedule schedule) {
        MaintenanceScheduleDTO dto = new MaintenanceScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setCustomerId(schedule.getCustomer() != null ? schedule.getCustomer().getCustomerId() : null);
        if (schedule.getVehicle() != null) {
            dto.setVehicleId(schedule.getVehicle().getVehicleId());
            dto.setVehicleModel(schedule.getVehicle().getModel());
            dto.setVehicleLicensePlate(schedule.getVehicle().getLicensePlate());
        }
        if (schedule.getServiceCenter() != null) {
            dto.setCenterId(schedule.getServiceCenter().getCenterId());
            dto.setCenterName(schedule.getServiceCenter().getName());
        }
        if (schedule.getMaintenancePackage() != null) {
            dto.setServiceId(schedule.getMaintenancePackage().getPackageId());
            dto.setServiceName(schedule.getMaintenancePackage().getName());
        }
        dto.setScheduledDate(schedule.getScheduledDate() != null
                ? schedule.getScheduledDate().toString()
                : null);
        dto.setScheduledTime(schedule.getScheduledTime() != null
                ? schedule.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                : null);
        dto.setStatus(schedule.getStatus());
        dto.setNotes(schedule.getNotes());
        return dto;
    }

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
                .technicianId(schedule.getTechnician() != null ? schedule.getTechnician().getTechnicianId() : null)
                .technicianName(schedule.getTechnician() != null ? schedule.getTechnician().getFullName() : null)
                .build();
    }

    private AppointmentResponse toAppointmentResponse(MaintenanceSchedule schedule) {
        return AppointmentResponse.builder()
                .id("lh" + schedule.getScheduleId())
                .dateTime(schedule.getScheduledDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .licensePlate(schedule.getVehicle().getLicensePlate())
                .customerName(schedule.getCustomer().getFullName())
                .centerName(schedule.getServiceCenter() != null ? schedule.getServiceCenter().getName() : "N/A")
                .status(schedule.getStatus())
                .action(getActionByStatus(schedule.getStatus()))
                .build();
    }

    private String getActionByStatus(String status) {
        return switch (status) {
            case "CHỜ_CHECKIN" -> "Check in";
            case "ĐANG_XỬ_LÝ" -> "Thanh toán";
            case "HOÀN_TẤT" -> "";
            default -> "";
        };
    }

    private String mapStatus(String status) {
        return switch (status) {
            case "ĐÃ_THANH_TOÁN" -> "Đã thanh toán";
            case "CHỜ_THANH_TOÁN" -> "Chờ thanh toán";
            default -> "";
        };
    }

    private String mapAction(String status) {
        return switch (status) {
            case "ĐÃ_THANH_TOÁN" -> "Xem hóa đơn";
            case "CHỜ_THANH_TOÁN" -> "In hóa đơn";
            default -> "";
        };
    }

    @Override
    public DashboardStatsResponse getDashboardStats(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // 1. Số lịch hẹn được ĐẶT trong ngày (đếm theo bookingDate)
        Long scheduledCount = scheduleRepository.findAll().stream()
                .filter(s -> s.getBookingDate() != null &&
                        !s.getBookingDate().isBefore(startOfDay) &&
                        s.getBookingDate().isBefore(endOfDay))
                .count();

        // 2. Xe đang bảo dưỡng (tất cả xe chưa hoàn thành - status KHÔNG PHẢI completed)
        List<String> completedStatuses = List.of("HOÀN_TẤT", "ĐÃ_THANH_TOÁN");
        Long overdueCount = scheduleRepository.findAll().stream()
                .filter(s -> !completedStatuses.contains(s.getStatus()))
                .count();

        // 3. Xe chờ nhận trả (Invoice status = UNPAID)
        // Đếm số invoice có status UNPAID
        Long pendingCount = invoiceRepository.findAll().stream()
                .filter(invoice -> "UNPAID".equals(invoice.getStatus()))
                .count();

        // 4. Thanh toán hoàn thành trong ngày (đếm theo bookingDate)
        Long completedCount = scheduleRepository.findAll().stream()
                .filter(s -> s.getBookingDate() != null &&
                        !s.getBookingDate().isBefore(startOfDay) &&
                        s.getBookingDate().isBefore(endOfDay) &&
                        completedStatuses.contains(s.getStatus()))
                .count();

        return new DashboardStatsResponse(
                scheduledCount.intValue(),
                overdueCount.intValue(),
                pendingCount.intValue(),
                completedCount.intValue()
        );
    }
}