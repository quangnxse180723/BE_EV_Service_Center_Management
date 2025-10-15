package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.CheckInRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.dto.response.StaffDashboardResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.StaffDashboardService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffDashboardSeviceImpl implements StaffDashboardService {

    private final StaffRepository staffRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final TechnicianRepository technicianRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public StaffDashboardResponse getDashboardStats(Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        LocalDateTime today = LocalDateTime.now();
        
        // 1. Số lịch hẹn hôm nay (màu xanh lá)
        Integer totalToday = scheduleRepository.countTodaySchedules(centerId, today);
        
        // 2. Xe cần sửa = PENDING + CONFIRMED (màu đỏ)
        Integer pending = scheduleRepository.countByServiceCenterAndStatus(centerId, "PENDING");
        Integer confirmed = scheduleRepository.countByServiceCenterAndStatus(centerId, "CONFIRMED");
        Integer vehiclesNeedRepair = pending + confirmed;
        
        // 3. Đã hoàn thành hôm nay (màu tím)
        Integer completed = scheduleRepository.countByServiceCenterAndStatus(centerId, "DONE");
        
        // 4. Tổng thanh toán hôm nay (màu vàng)
        Integer totalPayments = paymentRepository.countTodayPayments(centerId, today);
        BigDecimal totalRevenue = paymentRepository.sumTodayRevenue(centerId, today);
        
        // Thống kê technician
        Integer inProgress = scheduleRepository.countByServiceCenterAndStatus(centerId, "IN_PROGRESS");
        Integer available = technicianRepository.countAvailableTechnicians(centerId);
        Integer totalTechs = technicianRepository.findByServiceCenter_CenterId(centerId).size();
        
        return StaffDashboardResponse.builder()
                .totalSchedulesToday(totalToday)
                .vehiclesNeedRepair(vehiclesNeedRepair)
                .completedToday(completed)
                .totalPaymentsToday(totalPayments)
                .inProgressSchedules(inProgress)
                .availableTechnicians(available)
                .busyTechnicians(totalTechs - available)
                .totalRevenueToday(totalRevenue)
                .build();
    }

    @Override
    public List<ScheduleResponse> getTodaySchedules(Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        
        List<MaintenanceSchedule> schedules = scheduleRepository
                .findByServiceCenter_CenterIdAndScheduledDateBetween(centerId, startOfDay, endOfDay);
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getSchedulesByStatus(Integer staffId, String status) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        List<MaintenanceSchedule> schedules = scheduleRepository
                .findByServiceCenter_CenterIdAndStatus(centerId, status);
        
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse getScheduleDetail(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponse checkInVehicle(Integer staffId, CheckInRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        MaintenanceSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        // Validate schedule status
        if (!"CONFIRMED".equals(schedule.getStatus()) && !"PENDING".equals(schedule.getStatus())) {
            throw new RuntimeException("Schedule must be CONFIRMED or PENDING to check-in");
        }
        
        // Update schedule status
        schedule.setStatus("IN_PROGRESS");
        scheduleRepository.save(schedule);
        
        // Create maintenance record
        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceSchedule(schedule);
        record.setStaff(staff);
        record.setCheckInTime(LocalDateTime.now());
        record.setStatus("PENDING");
        record.setNote(request.getNotes());
        recordRepository.save(record);
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponse assignTechnician(Integer staffId, AssignTechnicianRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        MaintenanceSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        Technician technician = technicianRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        
        // Validate same service center
        if (!technician.getServiceCenter().getCenterId().equals(staff.getServiceCenter().getCenterId())) {
            throw new RuntimeException("Technician must be from the same service center");
        }
        
        // Find or create maintenance record
        MaintenanceRecord record = recordRepository
                .findByMaintenanceSchedule_ScheduleId(schedule.getScheduleId())
                .orElseGet(() -> {
                    MaintenanceRecord newRecord = new MaintenanceRecord();
                    newRecord.setMaintenanceSchedule(schedule);
                    newRecord.setStaff(staff);
                    newRecord.setCheckInTime(LocalDateTime.now());
                    newRecord.setStatus("IN_PROGRESS");
                    return newRecord;
                });
        
        // Assign technician
        record.setTechnician(technician);
        if (request.getNotes() != null) {
            record.setNote(request.getNotes());
        }
        recordRepository.save(record);
        
        // Update schedule status
        schedule.setStatus("IN_PROGRESS");
        scheduleRepository.save(schedule);
        
        return mapToScheduleResponse(schedule);
    }

    @Override
    public List<TechnicianResponse> getAvailableTechnicians(Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        List<Technician> technicians = technicianRepository.findAvailableTechnicians(centerId);
        
        return technicians.stream()
                .map(tech -> mapToTechnicianResponse(tech, 0))
                .collect(Collectors.toList());
    }

    @Override
    public List<TechnicianResponse> getAllTechnicians(Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        Integer centerId = staff.getServiceCenter().getCenterId();
        List<Technician> technicians = technicianRepository.findByServiceCenter_CenterId(centerId);
        
        return technicians.stream()
                .map(tech -> {
                    Integer activeCount = recordRepository.countActiveTasksByTechnician(tech.getTechnicianId());
                    return mapToTechnicianResponse(tech, activeCount);
                })
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
        
        // Technician info (from maintenance record)
        recordRepository.findByMaintenanceSchedule_ScheduleId(schedule.getScheduleId())
                .ifPresent(record -> {
                    if (record.getTechnician() != null) {
                        builder.technicianId(record.getTechnician().getTechnicianId())
                               .technicianName(record.getTechnician().getFullName());
                    }
                });
        
        return builder.build();
    }

    private TechnicianResponse mapToTechnicianResponse(Technician technician, Integer activeCount) {
        String status = (activeCount == 0) ? "AVAILABLE" : "BUSY";
        
        return TechnicianResponse.builder()
                .technicianId(technician.getTechnicianId())
                .fullName(technician.getFullName())
                .phone(technician.getPhone())
                .email(technician.getEmail())
                .status(status)
                .activeTasksCount(activeCount)
                .build();
    }
}
