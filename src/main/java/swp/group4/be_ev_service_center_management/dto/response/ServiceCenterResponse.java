package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCenterResponse {
    private Integer centerId;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String operatingHours;
}
