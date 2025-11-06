package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.ChecklistItemUpdateRequest;
import swp.group4.be_ev_service_center_management.dto.request.ChecklistApprovalRequest;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.MaintenanceChecklist;
import swp.group4.be_ev_service_center_management.entity.MaintenanceItem;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.repository.MaintenanceChecklistRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceItemRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceRecordRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceChecklistService;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceChecklistServiceImpl implements MaintenanceChecklistService {

    private final MaintenanceChecklistRepository checklistRepository;
    private final MaintenanceItemRepository itemRepository;
    private final NotificationService notificationService;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    @Override
    @Transactional
    public void submitForApproval(int checklistId) {
        MaintenanceChecklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("Checklist not found with ID: " + checklistId));

        MaintenanceSchedule schedule = checklist.getMaintenanceRecord().getMaintenanceSchedule();
        schedule.setStatus("WAITING_FOR_APPROVAL");
        maintenanceScheduleRepository.save(schedule);

        Account customerAccount = schedule.getCustomer().getAccount();
        if (customerAccount != null) {
            Integer customerId = customerAccount.getAccountId();
            String message = "Biên bản kiểm tra cho lịch hẹn #" + schedule.getScheduleId() + " đã sẵn sàng. Vui lòng phê duyệt.";
            String link = "/customer/approvals/" + checklistId;
            notificationService.createNotification(customerId, message, link);
        } else {
            // Handle the case where the customer does not have an associated account
            // For example, log a warning
            System.out.println("Warning: Customer with ID " + schedule.getCustomer().getCustomerId() + " does not have an associated account. Cannot send notification.");
        }
    }

    @Override
    @Transactional
    public void approveChecklist(int checklistId, ChecklistApprovalRequest request) {
        MaintenanceChecklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("Checklist not found with ID: " + checklistId));

        StringBuilder approvedItems = new StringBuilder();
        StringBuilder rejectedItems = new StringBuilder();
        int countApproved = 0;
        int countRejected = 0;

        for (ChecklistItemUpdateRequest itemUpdate : request.getItems()) {
            MaintenanceItem item = itemRepository.findById(itemUpdate.getItemId())
                    .orElseThrow(() -> new RuntimeException("Maintenance Item not found with ID: " + itemUpdate.getItemId()));

            if (item.getMaintenanceChecklist().getChecklistId() != checklistId) {
                throw new SecurityException("Item with ID " + itemUpdate.getItemId() + " does not belong to checklist " + checklistId);
            }

            String itemName = item.getName() != null ? item.getName() : 
                             (item.getPart() != null ? item.getPart().getName() : "Hạng mục");
            
            // Cập nhật trạng thái theo yêu cầu của khách hàng
            item.setDescription(itemUpdate.getActionStatus());
            
            // Logic giá tiền theo trạng thái
            if ("Kiểm tra".equalsIgnoreCase(itemUpdate.getActionStatus())) {
                // ✅ Kiểm tra (KT) → Không tính tiền (customer từ chối)
                item.setPartCost(java.math.BigDecimal.ZERO);
                item.setLaborCost(java.math.BigDecimal.ZERO);
                rejectedItems.append("- ").append(itemName).append(" (KT - không tính tiền)\n");
                countRejected++;
            } else if ("Bôi trơn".equalsIgnoreCase(itemUpdate.getActionStatus())) {
                // ✅ Bôi trơn (BT) → Không tính tiền phụ tùng, có tính công
                item.setPartCost(java.math.BigDecimal.ZERO);
                // Giữ nguyên laborCost (nếu có)
                approvedItems.append("- ").append(itemName).append(" (BT)\n");
                countApproved++;
            } else if ("Thay thế".equalsIgnoreCase(itemUpdate.getActionStatus())) {
                // ✅ Thay thế (TT) → Giữ nguyên giá gốc (đã tính +10%)
                approvedItems.append("- ").append(itemName).append(" (TT)\n");
                countApproved++;
            }
            
            item.setStatus("APPROVED");
            itemRepository.save(item);
        }

        // Cập nhật checklist
        checklist.setIsApproved(true);
        checklist.setApprovedDate(LocalDateTime.now());
        checklistRepository.save(checklist);

        // Cập nhật schedule và record
        MaintenanceSchedule schedule = checklist.getMaintenanceRecord().getMaintenanceSchedule();
        schedule.setStatus("APPROVED");
        maintenanceScheduleRepository.save(schedule);
        
        MaintenanceRecord record = checklist.getMaintenanceRecord();
        record.setStatus("APPROVED");
        maintenanceRecordRepository.save(record);

        // Tạo notification chi tiết cho kỹ thuật viên
        Account technicianAccount = checklist.getMaintenanceRecord().getTechnician().getAccount();
        if (technicianAccount != null) {
            Integer technicianId = technicianAccount.getAccountId();
            Integer customerId = schedule.getCustomer().getAccount().getAccountId();
            String vehicleInfo = schedule.getVehicle() != null 
                ? schedule.getVehicle().getModel() + " - " + schedule.getVehicle().getLicensePlate()
                : "xe";
            
            // ✅ Tạo message ngắn gọn (tránh vượt quá giới hạn VARCHAR)
            StringBuilder message = new StringBuilder();
            message.append("Khách hàng đã phê duyệt biên bản cho ").append(vehicleInfo);
            
            if (countApproved > 0 && countRejected > 0) {
                message.append(". Đồng ý: ").append(countApproved)
                       .append(" hạng mục, chỉ kiểm tra: ").append(countRejected).append(" hạng mục.");
            } else if (countApproved > 0) {
                message.append(". Đồng ý tất cả ").append(countApproved).append(" hạng mục.");
            } else if (countRejected > 0) {
                message.append(". Chỉ kiểm tra ").append(countRejected).append(" hạng mục.");
            }
            
            String link = "/technician/services/" + schedule.getScheduleId();
            
            notificationService.createNotificationForApproval(
                customerId,      // senderId (khách hàng)
                technicianId,    // receiverId (kỹ thuật viên)
                message.toString(),
                link,
                checklist.getMaintenanceRecord().getRecordId(),
                schedule.getScheduleId()
            );
        }
    }
}
