package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.AppointmentResponse;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;
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

        // Gán time slot (bắt buộc)
        if (request.getSlotId() != null) {
            TimeSlot timeSlot = timeSlotRepository.findById(request.getSlotId())
                    .orElseThrow(() -> new RuntimeException("Time Slot not found with ID: " + request.getSlotId()));
            schedule.setTimeSlot(timeSlot);
        } else {
            throw new RuntimeException("Time Slot ID is required");
        }

        // Lưu schedule
        MaintenanceSchedule savedSchedule = scheduleRepository.save(schedule);

        return toResponse(savedSchedule);
    }

    @Override
    public List<AppointmentResponse> getAppointments(String keyword) {
        List<MaintenanceSchedule> schedules = scheduleRepository.searchAppointments(keyword);
        return schedules.stream()
                .map(this::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentManagementResponse> getPaymentList() {
        List<String> statuses = List.of("CHỜ_THANH_TOÁN", "ĐÃ_THANH_TOÁN");
        List<MaintenanceSchedule> schedules = scheduleRepository.findForPayment(statuses);
        return schedules.stream().map(ms -> PaymentManagementResponse.builder()
                .customerName(ms.getCustomer().getFullName())
                .vehicleName(ms.getVehicle().getModel())
                .licensePlate(ms.getVehicle().getLicensePlate())
                .appointmentTime(ms.getScheduledDate().format(DateTimeFormatter.ofPattern("HH:mm")))
                .status(mapStatus(ms.getStatus()))
                .action(mapAction(ms.getStatus()))
                .build()
        ).collect(Collectors.toList());
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