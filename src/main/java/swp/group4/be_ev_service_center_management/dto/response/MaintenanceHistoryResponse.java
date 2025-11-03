package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MaintenanceHistoryResponse {
    private List<Integer> completedMaintenances; // VD: [1, 2, 3]
    private Integer nextRequiredMaintenance;     // VD: 4
    private Integer currentMaintenanceLevel;      // Dựa trên km/thời gian: VD: 5
    private Boolean isOverdue;                    // true nếu currentLevel > nextRequired
    private String overdueMessage;                // "Quá hạn lần 4" hoặc null
}
