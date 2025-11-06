package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class ChecklistItemUpdateRequest {
    private int itemId;
    private String actionStatus; // The new status, e.g., "Kiểm tra", "Bôi trơn"
}
