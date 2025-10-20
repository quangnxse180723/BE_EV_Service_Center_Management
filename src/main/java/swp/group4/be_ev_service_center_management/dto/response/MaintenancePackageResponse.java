package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePackageResponse {
    private Integer packageId;
    private String name;
    private Integer mileageMilestone;
    private String description;
}

