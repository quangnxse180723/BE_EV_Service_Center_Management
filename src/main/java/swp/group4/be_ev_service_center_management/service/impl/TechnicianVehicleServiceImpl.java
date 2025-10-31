package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.VehicleAssignmentResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.TechnicianVehicleService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianVehicleServiceImpl implements TechnicianVehicleService {
    
    private final MaintenanceScheduleRepository scheduleRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<VehicleAssignmentResponse> getAssignedVehicles(Integer technicianId) {
        List<MaintenanceSchedule> schedules = scheduleRepository.findByTechnician_TechnicianId(technicianId);
        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleAssignmentResponse> getAssignedVehiclesByStatus(Integer technicianId, String status) {
    List<MaintenanceSchedule> schedules = scheduleRepository.findByTechnician_TechnicianIdAndStatus(technicianId, status);
        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Helper: Map MaintenanceSchedule → VehicleAssignmentResponse
     */
    private VehicleAssignmentResponse toResponse(MaintenanceSchedule schedule) {
        return VehicleAssignmentResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .customerName(schedule.getCustomer().getFullName())
                .vehicleModel(schedule.getVehicle().getModel())
                .licensePlate(schedule.getVehicle().getLicensePlate())
                .ownerName(schedule.getCustomer().getFullName()) // Hoặc lấy từ Vehicle nếu có
                .status(schedule.getStatus())
                .scheduledDate(schedule.getScheduledDate() != null ? 
                        schedule.getScheduledDate().format(FORMATTER) : null)
                .action("edit,delete") // Frontend xử lý hiển thị nút
                .build();
    }
}
