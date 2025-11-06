package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.ChecklistApprovalRequest;

public interface MaintenanceChecklistService {
    void submitForApproval(int checklistId);
    void approveChecklist(int checklistId, ChecklistApprovalRequest request);
}
