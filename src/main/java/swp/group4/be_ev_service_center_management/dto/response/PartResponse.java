package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

@Data
public class PartResponse {
    private Integer partId;
    private Integer centerId;
    private String name;
    private String partCode;
    private Integer quantityInStock;
    private Integer minStock;
    private Double price;
}