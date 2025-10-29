package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketItemResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketListResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceTicketService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {
    
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceChecklistRepository checklistRepository;
    private final MaintenanceItemRepository itemRepository;

    @Override
    public List<ServiceTicketListResponse> getServiceTickets(Integer technicianId) {
        log.info("Fetching service tickets for technicianId: {}", technicianId);
        
        // Lấy tất cả schedules được phân công cho kỹ thuật viên
        List<MaintenanceSchedule> schedules = scheduleRepository.findByTechnician_TechnicianId(technicianId);
        
        log.info("Found {} schedules for technician {}", schedules.size(), technicianId);
        
        // Map sang ListResponse
        return schedules.stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }
    
    private ServiceTicketListResponse mapToListResponse(MaintenanceSchedule schedule) {
        return ServiceTicketListResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .customerName(getCustomerName(schedule))
                .vehicleModel(getVehicleModel(schedule))
                .licensePlate(getLicensePlate(schedule))
                .status(schedule.getStatus() != null ? schedule.getStatus() : "Chưa xử lý")
                .startTime(formatDateTime(schedule))
                .build();
    }

    @Override
    public ServiceTicketDetailResponse getServiceTicketDetail(Integer scheduleId) {
        log.info("Fetching service ticket detail for scheduleId: {}", scheduleId);
        
        // 1. Tìm schedule
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    log.error("Schedule not found with ID: {}", scheduleId);
                    return new RuntimeException("Schedule không tồn tại với ID: " + scheduleId);
                });
        
        // 2. Lấy record đầu tiên (1 schedule -> 1 record)
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream()
                .findFirst()
                .orElse(null);
        
        // 3. Build danh sách items
        List<ServiceTicketItemResponse> itemResponses = buildItemResponses(record);
        
        // 4. Build response
        return ServiceTicketDetailResponse.builder()
                .customerName(getCustomerName(schedule))
                .vehicleName(getVehicleModel(schedule))
                .licensePlate(getLicensePlate(schedule))
                .appointmentDateTime(formatDateTime(schedule))
                .items(itemResponses)
                .build();
    }
    
    private List<ServiceTicketItemResponse> buildItemResponses(MaintenanceRecord record) {
        List<ServiceTicketItemResponse> itemResponses = new ArrayList<>();
        
        if (record == null) {
            log.warn("No maintenance record found");
            return itemResponses;
        }
        
        List<MaintenanceChecklist> checklists = checklistRepository.findByMaintenanceRecord(record);
        int stt = 1;
        
        for (MaintenanceChecklist checklist : checklists) {
            List<MaintenanceItem> items = itemRepository.findByMaintenanceChecklist(checklist);
            
            for (MaintenanceItem item : items) {
                itemResponses.add(ServiceTicketItemResponse.builder()
                        .stt(stt++)
                        .partName(getPartName(item))
                        .actionStatus(getActionStatus(item))
                        .processStatus(getProcessStatus(item))
                        .confirmAction(getConfirmAction(item))
                        .build());
            }
        }
        
        log.info("Built {} item responses", itemResponses.size());
        return itemResponses;
    }
    
    private String getPartName(MaintenanceItem item) {
        if (item.getName() != null && !item.getName().trim().isEmpty()) {
            return item.getName();
        }
        if (item.getPart() != null && item.getPart().getName() != null) {
            return item.getPart().getName();
        }
        return "Chưa xác định";
    }
    
    private String getActionStatus(MaintenanceItem item) {
        return item.getDescription() != null && !item.getDescription().trim().isEmpty() 
            ? item.getDescription() 
            : "Chưa xác định";
    }
    
    private String getProcessStatus(MaintenanceItem item) {
        return item.getStatus() != null && !item.getStatus().trim().isEmpty() 
            ? item.getStatus() 
            : "Chưa xử lý";
    }
    
    private String getConfirmAction(MaintenanceItem item) {
        if (item.getStatus() != null && item.getStatus().equalsIgnoreCase("Hoàn thành")) {
            return "Xác nhận";
        }
        return "-";
    }
    
    private String getCustomerName(MaintenanceSchedule schedule) {
        return schedule.getCustomer() != null && schedule.getCustomer().getFullName() != null 
            ? schedule.getCustomer().getFullName() 
            : "N/A";
    }
    
    private String getVehicleModel(MaintenanceSchedule schedule) {
        return schedule.getVehicle() != null && schedule.getVehicle().getModel() != null 
            ? schedule.getVehicle().getModel() 
            : "N/A";
    }
    
    private String getLicensePlate(MaintenanceSchedule schedule) {
        return schedule.getVehicle() != null && schedule.getVehicle().getLicensePlate() != null 
            ? schedule.getVehicle().getLicensePlate() 
            : "N/A";
    }
    
    private String formatDateTime(MaintenanceSchedule schedule) {
        if (schedule.getScheduledDate() != null) {
            return schedule.getScheduledDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }
}