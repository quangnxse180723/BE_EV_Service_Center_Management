package swp.group4.be_ev_service_center_management.dto.request;

import lombok.Data;

@Data
public class PartRequest {
    private Integer centerId;
    private String name;
    private String partCode;
    private Integer quantityInStock;
    private Integer minStock;
    private Double price;
}