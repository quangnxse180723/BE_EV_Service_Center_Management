package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.ChecklistApprovalRequest;
import swp.group4.be_ev_service_center_management.service.interfaces.MaintenanceChecklistService;

@RestController
@RequestMapping("/api/maintenance-checklists")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenanceChecklistController {

    private final MaintenanceChecklistService checklistService;

    @PostMapping("/{checklistId}/submit-for-approval")
    public ResponseEntity<Void> submitForApproval(@PathVariable int checklistId) {
        checklistService.submitForApproval(checklistId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{checklistId}/approve")
    public ResponseEntity<Void> approveChecklist(
            @PathVariable int checklistId,
            @RequestBody ChecklistApprovalRequest request) {
        checklistService.approveChecklist(checklistId, request);
        return ResponseEntity.ok().build();
    }
}
