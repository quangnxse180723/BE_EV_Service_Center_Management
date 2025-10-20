package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookScheduleRequest {

    @NotNull(message = "Vehicle ID is required")
    private Integer vehicleId;

    @NotNull(message = "Service Center ID is required")
    private Integer centerId;

    @NotNull(message = "Time Slot ID is required")
    private Integer slotId;

    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;

    private Integer packageId; // Optional - gói bảo dưỡng

    private String notes; // Ghi chú từ khách hàng
}

