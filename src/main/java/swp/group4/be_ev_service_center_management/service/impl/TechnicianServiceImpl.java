package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianDashboardResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.entity.Technician;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.TechnicianRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.TechnicianService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;

    @Override
    public List<TechnicianResponse> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::toTechnicianResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TechnicianDashboardResponse getDashboardStats(Integer technicianId, LocalDate date) {
        // Nếu không có date, dùng ngày hôm nay
        if (date == null) {
            date = LocalDate.now();
        }

        // Lấy tất cả schedule của technician
        List<MaintenanceSchedule> allSchedules = maintenanceScheduleRepository
                .findByTechnician_TechnicianId(technicianId);

        LocalDate finalDate = date;
        
        // 1. Đếm số xe đang xử lý (IN_PROGRESS)
        long overdueCount = allSchedules.stream()
                .filter(s -> "IN_PROGRESS".equalsIgnoreCase(s.getStatus()))
                .count();

        // 2. Đếm số công việc trong ngày (PENDING, CONFIRMED trong ngày)
        long workingCount = allSchedules.stream()
                .filter(s -> {
                    String status = s.getStatus();
                    LocalDateTime scheduledDate = s.getScheduledDate();
                    
                    // Kiểm tra status và ngày
                    boolean isWorkingStatus = "PENDING".equalsIgnoreCase(status) 
                                           || "CONFIRMED".equalsIgnoreCase(status)
                                           || "IN_PROGRESS".equalsIgnoreCase(status);
                    
                    boolean isToday = scheduledDate != null 
                                   && scheduledDate.toLocalDate().equals(finalDate);
                    
                    return isWorkingStatus && isToday;
                })
                .count();

        // 3. Đếm số lịch phân công sắp tới (PENDING, CONFIRMED trong tương lai)
        LocalDateTime now = LocalDateTime.now();
        
        long scheduleCount = allSchedules.stream()
                .filter(s -> {
                    String status = s.getStatus();
                    LocalDateTime scheduledDate = s.getScheduledDate();
                    
                    boolean isPendingOrConfirmed = "PENDING".equalsIgnoreCase(status) 
                                                || "CONFIRMED".equalsIgnoreCase(status);
                    
                    boolean isFuture = scheduledDate != null 
                                    && scheduledDate.isAfter(now);
                    
                    return isPendingOrConfirmed && isFuture;
                })
                .count();

        return TechnicianDashboardResponse.builder()
                .overdueCount((int) overdueCount)
                .workingCount((int) workingCount)
                .scheduleCount((int) scheduleCount)
                .build();
    }

    private TechnicianResponse toTechnicianResponse(Technician technician) {
        return TechnicianResponse.builder()
                .technicianId(technician.getTechnicianId())
                .serviceCenterId(technician.getServiceCenter() != null ? technician.getServiceCenter().getCenterId() : null)
                .accountId(technician.getAccount() != null ? technician.getAccount().getAccountId() : null)
                .fullName(technician.getFullName())
                .phone(technician.getPhone())
                .email(technician.getEmail())
                .createdAt(technician.getCreatedAt())
                .build();
    }
}