package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChecklistItemResponse {
    private Integer stt;  // Số thứ tự
    private String partName;  // Tên phụ tùng
    private String description;  // Hành động: "Kiểm tra", "Thay thế", "Bôi trơn"
    private String status;  // Trạng thái xử lý: "PENDING", "DONE", "APPROVED"
    private Integer materialCost;  // Vật tư (vnđ)
    private Integer laborCost;  // Nhân công (vnđ)
}
