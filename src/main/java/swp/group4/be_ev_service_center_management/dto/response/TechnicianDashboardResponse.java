package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDashboardResponse {
    private Integer overdueCount;      // Số xe đang xử lý (overdue/in-progress)
    private Integer workingCount;      // Số công việc trong ngày
    private Integer scheduleCount;     // Số lịch phân công (upcoming schedules)
}
