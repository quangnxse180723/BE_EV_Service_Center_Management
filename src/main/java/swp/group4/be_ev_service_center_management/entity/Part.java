package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "part")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Integer partId;
    
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private ServiceCenter serviceCenter;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "part_code", length = 50)
    private String partCode;
    
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;
    
    @Column(name = "min_stock")
    private Integer minStock;
    
    @Column(name = "price", precision = 18, scale = 2)
    private BigDecimal price;
}
