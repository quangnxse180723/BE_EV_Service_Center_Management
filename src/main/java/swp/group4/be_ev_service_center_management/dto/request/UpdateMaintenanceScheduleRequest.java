package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMaintenanceScheduleRequest {

    @NotBlank(message = "Status is required")
    private String status; // "Chờ xác nhận", "Đang thực hiện", "Hoàn tất", "Hủy"
    private Integer technicianId;
    private String notes; // Ghi chú của nhân viên
}