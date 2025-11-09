package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Integer scheduledCount;    // Số lịch hẹn trong ngày
    private Integer overdueCount;      // Xe đang bảo dưỡng (quá hạn)
    private Integer pendingCount;      // Xe chờ nhận trả
    private Integer completedCount;    // Thanh toán hoàn thành trong ngày
}
