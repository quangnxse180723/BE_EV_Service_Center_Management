package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChecklistItemResponse {
    private Integer stt;  // Số thứ tự
    private Integer itemId;  // ID của maintenance item
    private String partCode;  // Mã phụ tùng (PT001, PT002...)
    private String partName;  // Tên phụ tùng
    private String description;  // Hành động: "Kiểm tra", "Thay thế", "Bôi trơn"
    private String status;  // Trạng thái xử lý: "PENDING", "DONE", "APPROVED"
    private Integer materialCost;  // Vật tư (vnđ) - Giá hiện tại
    private Integer laborCost;  // Nhân công (vnđ) - Giá hiện tại
    private Integer originalPartCost;  // Giá gốc vật tư từ bảng part
    private Integer originalLaborCost;  // Giá gốc nhân công mặc định
}
