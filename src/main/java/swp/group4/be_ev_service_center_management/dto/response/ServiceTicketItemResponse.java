package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceTicketItemResponse {
    private Integer stt;
    private String partName;
    private String actionStatus; // Trạng thái xử lý (VD: Thay thế, Kiểm tra, Bôi trơn...)
    private String processStatus; // Trạng thái tiến trình (VD: Đang xử lý, Hoàn thành...)
    private String confirmAction; // VD: Xác nhận
}
