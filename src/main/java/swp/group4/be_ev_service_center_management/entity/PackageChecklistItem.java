package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "packagechecklistitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageChecklistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;
    
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private MaintenancePackage maintenancePackage;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = true) // Cho phép null vì có thể không cần vật tư
    private Part part;
    
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    
    @Column(name = "item_description", length = 255)
    private String itemDescription;
    
    @Column(name = "default_labor_cost", precision = 18, scale = 2)
    private BigDecimal defaultLaborCost;
}
