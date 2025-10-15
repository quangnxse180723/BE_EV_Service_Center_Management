package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MaintenanceRecordResponse {
    private Integer recordId;
    private Integer scheduleId;
    private Integer staffId;
    private Integer technicianId;
    private Date checkInTime;
    private Date checkOutTime;
    private Integer mileageAtService;
    private String status;
    private Double totalCost;
    private String note;
    private List<MaintenanceItemResponse> items;

    @Data
    public static class MaintenanceItemResponse {
        private Integer itemId;
        private String name;
        private String status;
        private Double laborCost;
        private Double partCost;
    }
}