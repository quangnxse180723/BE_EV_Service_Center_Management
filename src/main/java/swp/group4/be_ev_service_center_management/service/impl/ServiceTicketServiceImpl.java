package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketItemResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceTicketService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceChecklistRepository checklistRepository;
    private final MaintenanceItemRepository itemRepository;

    @Override
    public ServiceTicketDetailResponse getServiceTicketDetail(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        // Lấy record đầu tiên (giả định 1 schedule chỉ có 1 record)
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream().findFirst().orElse(null);
        List<ServiceTicketItemResponse> itemResponses = new ArrayList<>();
        if (record != null) {
            List<MaintenanceChecklist> checklists = checklistRepository.findByMaintenanceRecord(record);
            int stt = 1;
            for (MaintenanceChecklist checklist : checklists) {
                List<MaintenanceItem> items = itemRepository.findByMaintenanceChecklist(checklist);
                for (MaintenanceItem item : items) {
                    itemResponses.add(ServiceTicketItemResponse.builder()
                            .stt(stt++)
                            .partName(item.getName() != null ? item.getName() : (item.getPart() != null ? item.getPart().getName() : ""))
                            .actionStatus(item.getDescription())
                            .processStatus(item.getStatus())
                            .confirmAction("Xác nhận")
                            .build());
                }
            }
        }
        return ServiceTicketDetailResponse.builder()
                .customerName(schedule.getCustomer().getFullName())
                .vehicleName(schedule.getVehicle().getModel())
                .licensePlate(schedule.getVehicle().getLicensePlate())
                .appointmentDateTime(schedule.getScheduledDate() != null ? schedule.getScheduledDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "")
                .items(itemResponses)
                .build();
    }
}
