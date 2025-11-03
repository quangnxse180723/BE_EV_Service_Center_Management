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
import swp.group4.be_ev_service_center_management.entity.Part;
import swp.group4.be_ev_service_center_management.repository.MaintenanceChecklistRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceItemRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceRecordRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.PartRepository;
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
    private final PartRepository partRepository;

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

                // Lấy giá gốc từ bảng part
                int originalPartCost = 0;
                int originalLaborCost = 50000; // Default labor cost
                String partCode = null;

                if (item.getPart() != null) {
                    Part part = item.getPart();
                    originalPartCost = part.getPrice() != null ? part.getPrice().intValue() : 0;
                    partCode = part.getPartCode();
                }

                items.add(ChecklistItemResponse.builder()
                        .stt(stt++)
                        .itemId(item.getItemId())
                        .partCode(partCode)
                        .partName(getPartName(item))
                        .description(getItemDescription(item))
                        .status(getItemStatus(item))
                        .materialCost(materialCost)
                        .laborCost(laborCost)
                        .originalPartCost(originalPartCost)
                        .originalLaborCost(originalLaborCost)
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
                existingItem.setDescription(itemRequest.getStatus() != null ? itemRequest.getStatus() : "Kiểm tra");

                // Logic tự động tính giá dựa trên description
                if ("Thay thế".equals(itemRequest.getStatus())) {
                    // Lấy giá từ bảng Part
                    Part part = partRepository.findByName(itemRequest.getPartName())
                            .orElse(null);

                    if (part != null) {
                        existingItem.setPart(part);
                        existingItem.setPartCost(part.getPrice());
                        // Labor cost cố định cho thay thế (hoặc lấy từ config)
                        existingItem.setLaborCost(itemRequest.getLaborCost() != null
                                ? BigDecimal.valueOf(itemRequest.getLaborCost())
                                : BigDecimal.valueOf(50000)); // Default 50k
                        log.info("Set price from Part: {} = {}", part.getName(), part.getPrice());
                    } else {
                        // Fallback: dùng giá từ request
                        existingItem.setPartCost(itemRequest.getMaterialCost() != null
                                ? BigDecimal.valueOf(itemRequest.getMaterialCost()) : BigDecimal.ZERO);
                        existingItem.setLaborCost(itemRequest.getLaborCost() != null
                                ? BigDecimal.valueOf(itemRequest.getLaborCost()) : BigDecimal.ZERO);
                        log.warn("Part not found in inventory: {}", itemRequest.getPartName());
                    }
                } else {
                    // Kiểm tra hoặc Bôi trơn → Giá = 0
                    existingItem.setPartCost(BigDecimal.ZERO);
                    existingItem.setLaborCost(BigDecimal.ZERO);
                    log.info("Set price to 0 for: {}", itemRequest.getStatus());
                }

                itemRepository.save(existingItem);
                log.info("Updated item ID: {}", existingItem.getItemId());
            } else {
                // Create new item
                MaintenanceItem newItem = new MaintenanceItem();
                newItem.setMaintenanceChecklist(checklist);
                newItem.setName(itemRequest.getPartName());
                newItem.setDescription(itemRequest.getStatus() != null ? itemRequest.getStatus() : "Kiểm tra");
                newItem.setStatus("PENDING");

                // Logic tự động tính giá
                if ("Thay thế".equals(itemRequest.getStatus())) {
                    Part part = partRepository.findByName(itemRequest.getPartName()).orElse(null);
                    if (part != null) {
                        newItem.setPart(part);
                        newItem.setPartCost(part.getPrice());
                        newItem.setLaborCost(BigDecimal.valueOf(50000)); // Default
                    } else {
                        newItem.setPartCost(BigDecimal.ZERO);
                        newItem.setLaborCost(BigDecimal.ZERO);
                    }
                } else {
                    newItem.setPartCost(BigDecimal.ZERO);
                    newItem.setLaborCost(BigDecimal.ZERO);
                }

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