package swp.group4.be_ev_service_center_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketItemResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketListResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.ServiceTicketService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceChecklistRepository checklistRepository;
    private final MaintenanceItemRepository itemRepository;
    private final PackageChecklistItemRepository packageItemRepository;
    private final InvoiceRepository invoiceRepository; // ✅ Thêm InvoiceRepository

    @Override
    public List<ServiceTicketListResponse> getServiceTickets(Integer technicianId) {
        log.info("Fetching service tickets for technicianId: {}", technicianId);
        List<MaintenanceSchedule> schedules = scheduleRepository.findByTechnician_TechnicianId(technicianId);
        log.info("Found {} schedules for technician {}", schedules.size(), technicianId);
        return schedules.stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceTicketDetailResponse getServiceTicketDetail(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));

        MaintenanceRecord record = getOrCreateMaintenanceRecord(schedule);
        List<MaintenanceItem> maintenanceItems = getOrCreateChecklistItems(record, schedule);

        List<ServiceTicketItemResponse> itemResponses = maintenanceItems.stream()
                .map(this::toServiceTicketItemResponse)
                .collect(Collectors.toList());

        // Lấy checklistId từ record
        Integer checklistId = null;
        Optional<MaintenanceChecklist> checklist = checklistRepository.findByMaintenanceRecord(record).stream().findFirst();
        if (checklist.isPresent()) {
            checklistId = checklist.get().getChecklistId();
        }

        return ServiceTicketDetailResponse.builder()
                .customerName(getCustomerName(schedule))
                .vehicleName(getVehicleModel(schedule))
                .licensePlate(getLicensePlate(schedule))
                .appointmentDateTime(formatDateTime(schedule))
                .checklistId(checklistId)
                .items(itemResponses)
                .build();
    }

    private MaintenanceRecord getOrCreateMaintenanceRecord(MaintenanceSchedule schedule) {
        return recordRepository.findByMaintenanceSchedule(schedule).stream().findFirst()
                .orElseGet(() -> {
                    log.info("Creating new MaintenanceRecord for scheduleId: {}", schedule.getScheduleId());
                    MaintenanceRecord newRecord = new MaintenanceRecord();
                    newRecord.setMaintenanceSchedule(schedule);
                    newRecord.setStatus("IN_PROGRESS");
                    newRecord.setTechnician(schedule.getTechnician());
                    newRecord.setCheckInTime(LocalDateTime.now());
                    return recordRepository.save(newRecord);
                });
    }

    private List<MaintenanceItem> getOrCreateChecklistItems(MaintenanceRecord record, MaintenanceSchedule schedule) {
        Optional<MaintenanceChecklist> existingChecklist = checklistRepository.findByMaintenanceRecord(record).stream().findFirst();

        if (existingChecklist.isPresent()) {
            log.info("Found existing MaintenanceChecklist, loading items...");
            return itemRepository.findByMaintenanceChecklist(existingChecklist.get());
        }

        log.info("Creating new MaintenanceChecklist from template for scheduleId: {}", schedule.getScheduleId());
        MaintenanceChecklist newChecklist = new MaintenanceChecklist();
        newChecklist.setMaintenanceRecord(record);
        newChecklist.setSummary("Checklist created automatically from package");
        MaintenanceChecklist savedChecklist = checklistRepository.save(newChecklist);

        MaintenancePackage maintenancePackage = schedule.getMaintenancePackage();
        if (maintenancePackage == null) {
            log.warn("Schedule with ID {} has no MaintenancePackage. Creating an empty checklist.", schedule.getScheduleId());
            return new ArrayList<>();
        }

        log.info("Found package: {}. Copying template items...", maintenancePackage.getName());
        List<PackageChecklistItem> templateItems = packageItemRepository.findByMaintenancePackage(maintenancePackage);

        List<MaintenanceItem> newItems = templateItems.stream().map(templateItem -> {
            MaintenanceItem newItem = new MaintenanceItem();
            newItem.setMaintenanceChecklist(savedChecklist);
            newItem.setName(templateItem.getItemName());
            
            // ✅ Logic: Nếu có Part → Thay thế, không có Part → Kiểm tra
            if (templateItem.getPart() != null) {
                newItem.setPart(templateItem.getPart());
                // Lấy giá từ Part và cộng 10%
                BigDecimal partPrice = templateItem.getPart().getPrice();
                BigDecimal partCostWithMarkup = partPrice.multiply(BigDecimal.valueOf(1.1));
                newItem.setPartCost(partCostWithMarkup);
                newItem.setDescription("Thay thế"); // ✅ Set status = TT khi có Part
                newItem.setLaborCost(templateItem.getDefaultLaborCost() != null 
                        ? templateItem.getDefaultLaborCost() 
                        : BigDecimal.valueOf(50000)); // Default 50k
            } else {
                newItem.setPartCost(BigDecimal.ZERO);
                newItem.setDescription(templateItem.getItemDescription() != null 
                        ? templateItem.getItemDescription() 
                        : "Kiểm tra"); // ✅ Dùng description từ template hoặc mặc định KT
                newItem.setLaborCost(templateItem.getDefaultLaborCost() != null 
                        ? templateItem.getDefaultLaborCost() 
                        : BigDecimal.ZERO);
            }

            newItem.setStatus("PENDING");
            return newItem;
        }).collect(Collectors.toList());

        if (!newItems.isEmpty()) {
            return itemRepository.saveAll(newItems);
        }

        return new ArrayList<>();
    }

    private ServiceTicketItemResponse toServiceTicketItemResponse(MaintenanceItem item) {
        Part part = item.getPart();
        return ServiceTicketItemResponse.builder()
                .itemId(item.getItemId()) // ✅ Trả về itemId để frontend có thể update
                .stt(item.getItemId())    // Số thứ tự (có thể giữ hoặc đổi logic)
                .partCode(part != null ? part.getPartCode() : null)
                .partName(getPartName(item))
                .partCost(item.getPartCost() != null ? item.getPartCost() : BigDecimal.ZERO)
                .laborCost(item.getLaborCost() != null ? item.getLaborCost() : BigDecimal.ZERO)
                .actionStatus(getActionStatus(item))
                .processStatus(getProcessStatus(item))
                .confirmAction("Xác nhận")
                .build();
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

    private String getPartName(MaintenanceItem item) {
        if (item.getPart() != null && item.getPart().getName() != null) {
            return item.getPart().getName();
        }
        if (item.getName() != null && !item.getName().trim().isEmpty()) {
            return item.getName();
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
    
    @Override
    @Transactional
    public void confirmItemCompletion(Integer itemId) {
        log.info("Confirming completion for item ID: {}", itemId);
        MaintenanceItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        
        item.setStatus("DONE");
        itemRepository.save(item);
        log.info("Item {} marked as DONE", itemId);
    }
    
    @Override
    @Transactional
    public void completeSchedule(Integer scheduleId) {
        log.info("Completing schedule ID: {}", scheduleId);
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
        
        schedule.setStatus("COMPLETED");
        scheduleRepository.save(schedule);
        
        // Cũng update record nếu có
        Optional<MaintenanceRecord> recordOpt = recordRepository.findByMaintenanceSchedule(schedule).stream().findFirst();
        if (recordOpt.isPresent()) {
            MaintenanceRecord record = recordOpt.get();
            record.setStatus("COMPLETED");
            record.setCheckOutTime(LocalDateTime.now());
            recordRepository.save(record);
            log.info("Record {} marked as COMPLETED", record.getRecordId());
            
            // ✅ Tự động tạo Invoice nếu chưa có
            createInvoiceIfNotExists(record);
        }
        
        log.info("Schedule {} marked as COMPLETED", scheduleId);
    }
    
    /**
     * Tự động tạo Invoice cho MaintenanceRecord nếu chưa tồn tại
     */
    private void createInvoiceIfNotExists(MaintenanceRecord record) {
        // Kiểm tra xem invoice đã tồn tại chưa
        Optional<Invoice> existingInvoice = invoiceRepository.findByMaintenanceRecord(record);
        
        if (existingInvoice.isEmpty()) {
            // Tính tổng chi phí từ các items
            Optional<MaintenanceChecklist> checklistOpt = checklistRepository.findByMaintenanceRecord(record).stream().findFirst();
            
            BigDecimal totalLaborCost = BigDecimal.ZERO;
            BigDecimal totalPartCost = BigDecimal.ZERO;
            
            if (checklistOpt.isPresent()) {
                List<MaintenanceItem> items = itemRepository.findByMaintenanceChecklist(checklistOpt.get());
                totalLaborCost = items.stream()
                        .map(item -> item.getLaborCost() != null ? item.getLaborCost() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalPartCost = items.stream()
                        .map(item -> item.getPartCost() != null ? item.getPartCost() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            
            // Tạo Invoice mới
            Invoice invoice = new Invoice();
            invoice.setMaintenanceRecord(record);
            invoice.setTotalLaborCost(totalLaborCost);
            invoice.setTotalPartCost(totalPartCost);
            invoice.setStatus("UNPAID");
            invoiceRepository.save(invoice);
            
            log.info("✅ Auto-generated invoice for record {} with total: {} (labor) + {} (parts)", 
                    record.getRecordId(), totalLaborCost, totalPartCost);
        } else {
            log.info("Invoice already exists for record {}", record.getRecordId());
        }
    }
}
