package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for staff dashboard statistics
 * Khớp với UI: Số lịch hẹn hôm nay, Xe cần sửa, Đã hoàn thành, Tổng thanh toán hôm nay
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDashboardResponse {
    
    // Số lịch hẹn hôm nay (màu xanh lá)
    private Integer totalSchedulesToday;
    
    // Xe cần sửa = PENDING + CONFIRMED (màu đỏ)
    private Integer vehiclesNeedRepair;
    
    // Đã hoàn thành hôm nay (màu tím)
    private Integer completedToday;
    
    // Tổng thanh toán hôm nay (màu vàng) - số lượng payment
    private Integer totalPaymentsToday;
    
    // Chi tiết thêm
    private Integer inProgressSchedules;
    private Integer availableTechnicians;
    private Integer busyTechnicians;
    
    // Tổng tiền thu được hôm nay (optional)
    private BigDecimal totalRevenueToday;
}
