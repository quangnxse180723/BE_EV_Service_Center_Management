package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response chi tiết invoice cho Staff (bao gồm cả biên bản sửa chữa)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDetailForStaffResponse {
    
    // === THÔNG TIN KHÁCH HÀNG ===
    private String customerName;
    private String vehicleName;        // VinFast Feliz S
    private String licensePlate;       // Biển số xe
    
    // === THÔNG TIN NHÂN VIÊN ===
    private String technicianName;     // Tên nhân viên 1
    private LocalDate inspectionDate;  // Ngày in hóa đơn
    
    // === THÔNG TIN BIÊN BẢN SỬA CHỮA ===
    private String status;             // Chi tiết / Chờ thanh toán / Đã thanh toán
    private List<MaintenanceItemDetail> items;  // Danh sách hạng mục bảo dưỡng
    
    // === THÔNG TIN THANH TOÁN ===
    private BigDecimal totalCost;      // 645,000 vnd
    private String paymentStatus;      // Chờ thanh toán / Ngân hàng / Tiền mặt
    private String paymentMethod;      // BANK / CASH / null
    
    // === IDS ===
    private Integer scheduleId;
    private Integer invoiceId;
    private Integer customerId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaintenanceItemDetail {
        private Integer itemId;
        private String itemName;           // Tay phanh / Đèn cói/ hiển thị động hồ
        private String actionStatus;       // KT / BT / TT
        private BigDecimal laborCost;      // Chi phí công
        private BigDecimal partCost;       // Chi phí phụ tùng
        private Integer mileage;           // x1000 Km (1, 5, 10, 15...)
        private Integer months;            // Tháng (1, 6, 12, 18...)
    }
}
