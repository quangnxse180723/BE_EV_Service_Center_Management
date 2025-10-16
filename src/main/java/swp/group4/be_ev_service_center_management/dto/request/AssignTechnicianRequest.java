package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTechnicianRequest {

    @NotNull(message = "Technician ID is required")
    private Integer technicianId;

}