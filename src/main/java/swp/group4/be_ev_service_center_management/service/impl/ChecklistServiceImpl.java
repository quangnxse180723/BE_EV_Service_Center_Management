package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.ChecklistItemRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateChecklistRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChecklistItemResponse;
import swp.group4.be_ev_service_center_management.dto.response.ChecklistResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceChecklist;
import swp.group4.be_ev_service_center_management.entity.MaintenanceItem;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.repository.MaintenanceChecklistRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceItemRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceRecordRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.ChecklistService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {
    
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceChecklistRepository checklistRepository;
    private final MaintenanceItemRepository itemRepository;

    @Override
    public ChecklistResponse getChecklistByScheduleId(Integer scheduleId) {
        log.info("Fetching checklist for scheduleId: {}", scheduleId);
        
        // 1. Tìm schedule
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    log.error("Schedule not found with ID: {}", scheduleId);
                    return new RuntimeException("Schedule không tồn tại với ID: " + scheduleId);
                });
        
        // 2. Lấy record
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No maintenance record found for scheduleId: {}", scheduleId);
                    return new RuntimeException("Chưa có biên bản kiểm tra cho schedule này");
                });
        
        // 3. Lấy tất cả checklists
        List<MaintenanceChecklist> checklists = checklistRepository.findByMaintenanceRecord(record);
        
        // 4. Build response
        List<ChecklistItemResponse> items = new ArrayList<>();
        int stt = 1;
        int totalMaterialCost = 0;
        int totalLaborCost = 0;
        
        for (MaintenanceChecklist checklist : checklists) {
            List<MaintenanceItem> maintenanceItems = itemRepository.findByMaintenanceChecklist(checklist);
            
            for (MaintenanceItem item : maintenanceItems) {
                int materialCost = item.getPartCost() != null ? item.getPartCost().intValue() : 0;
                int laborCost = item.getLaborCost() != null ? item.getLaborCost().intValue() : 0;
                
                totalMaterialCost += materialCost;
                totalLaborCost += laborCost;
                
                items.add(ChecklistItemResponse.builder()
                        .stt(stt++)
                        .partName(getPartName(item))
                        .description(getItemDescription(item))
                        .status(getItemStatus(item))
                        .materialCost(materialCost)
                        .laborCost(laborCost)
                        .build());
            }
        }
        
        log.info("Found {} checklist items", items.size());
        
        return ChecklistResponse.builder()
                .items(items)
                .materialCost(totalMaterialCost)
                .laborCost(totalLaborCost)
                .totalCost(totalMaterialCost + totalLaborCost)
                .build();
    }
    
    @Override
    @Transactional
    public ChecklistResponse updateChecklist(Integer scheduleId, UpdateChecklistRequest request) {
        log.info("Updating checklist for scheduleId: {}", scheduleId);
        
        // 1. Tìm schedule và record
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule không tồn tại với ID: " + scheduleId));
        
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Chưa có biên bản kiểm tra cho schedule này"));
        
        // 2. Lấy checklist hiện tại (hoặc tạo mới nếu chưa có)
        List<MaintenanceChecklist> checklists = checklistRepository.findByMaintenanceRecord(record);
        MaintenanceChecklist checklist;
        
        if (checklists.isEmpty()) {
            // Tạo mới checklist
            checklist = new MaintenanceChecklist();
            checklist.setMaintenanceRecord(record);
            checklist.setSummary("Checklist created");
            checklist = checklistRepository.save(checklist);
            log.info("Created new checklist with ID: {}", checklist.getChecklistId());
        } else {
            checklist = checklists.get(0);
        }
        
        // 3. Xử lý từng item trong request
        for (ChecklistItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getItemId() != null) {
                // Update existing item
                MaintenanceItem existingItem = itemRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item không tồn tại: " + itemRequest.getItemId()));
                
                existingItem.setName(itemRequest.getPartName());
                existingItem.setStatus(itemRequest.getStatus() != null ? itemRequest.getStatus() : "PENDING");
                existingItem.setPartCost(itemRequest.getMaterialCost() != null 
                        ? BigDecimal.valueOf(itemRequest.getMaterialCost()) : BigDecimal.ZERO);
                existingItem.setLaborCost(itemRequest.getLaborCost() != null 
                        ? BigDecimal.valueOf(itemRequest.getLaborCost()) : BigDecimal.ZERO);
                
                itemRepository.save(existingItem);
                log.info("Updated item ID: {}", existingItem.getItemId());
            } else {
                // Create new item
                MaintenanceItem newItem = new MaintenanceItem();
                newItem.setMaintenanceChecklist(checklist);
                newItem.setName(itemRequest.getPartName());
                newItem.setStatus(itemRequest.getStatus() != null ? itemRequest.getStatus() : "PENDING");
                newItem.setPartCost(itemRequest.getMaterialCost() != null 
                        ? BigDecimal.valueOf(itemRequest.getMaterialCost()) : BigDecimal.ZERO);
                newItem.setLaborCost(itemRequest.getLaborCost() != null 
                        ? BigDecimal.valueOf(itemRequest.getLaborCost()) : BigDecimal.ZERO);
                
                itemRepository.save(newItem);
                log.info("Created new item: {}", newItem.getName());
            }
        }
        
        // 4. Trả về checklist đã cập nhật
        return getChecklistByScheduleId(scheduleId);
    }
    
    @Override
    @Transactional
    public void submitForApproval(Integer scheduleId) {
        log.info("Submitting checklist for approval, scheduleId: {}", scheduleId);
        
        // 1. Tìm schedule và record
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule không tồn tại với ID: " + scheduleId));
        
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Chưa có biên bản kiểm tra cho schedule này"));
        
        // 2. Cập nhật status thành WAITING_APPROVE
        record.setStatus("WAITING_APPROVE");
        
        // 3. Tính tổng chi phí từ các items
        List<MaintenanceChecklist> checklists = checklistRepository.findByMaintenanceRecord(record);
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (MaintenanceChecklist checklist : checklists) {
            List<MaintenanceItem> items = itemRepository.findByMaintenanceChecklist(checklist);
            for (MaintenanceItem item : items) {
                if (item.getPartCost() != null) {
                    totalCost = totalCost.add(item.getPartCost());
                }
                if (item.getLaborCost() != null) {
                    totalCost = totalCost.add(item.getLaborCost());
                }
            }
        }
        
        record.setTotalCost(totalCost);
        recordRepository.save(record);
        
        log.info("Record status updated to WAITING_APPROVE with total cost: {}", totalCost);
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
    
    private String getItemDescription(MaintenanceItem item) {
        // Trả về description: "Kiểm tra", "Thay thế", "Bôi trơn"
        return item.getDescription() != null && !item.getDescription().trim().isEmpty() 
            ? item.getDescription() 
            : "Kiểm tra";
    }
    
    private String getItemStatus(MaintenanceItem item) {
        // Trả về status: "PENDING", "DONE", "APPROVED"
        return item.getStatus() != null && !item.getStatus().trim().isEmpty() 
            ? item.getStatus() 
            : "PENDING";
    }
}
