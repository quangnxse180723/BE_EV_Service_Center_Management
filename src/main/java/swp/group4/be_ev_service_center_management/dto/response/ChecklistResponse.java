package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChecklistResponse {
    private List<ChecklistItemResponse> items;
    private Integer totalCost;  // Tổng chi phí
    private Integer laborCost;  // Chi phí nhân công
    private Integer materialCost;  // Chi phí vật tư
}
