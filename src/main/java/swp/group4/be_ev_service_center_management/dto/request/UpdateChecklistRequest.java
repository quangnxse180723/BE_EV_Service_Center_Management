package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateChecklistRequest {
    private List<ChecklistItemRequest> items;
}
