package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class ChecklistItemRequest {
    private Integer itemId;  // null nếu là item mới
    private String partName;
    private String status;
    private Integer materialCost;  // part_cost
    private Integer laborCost;
}