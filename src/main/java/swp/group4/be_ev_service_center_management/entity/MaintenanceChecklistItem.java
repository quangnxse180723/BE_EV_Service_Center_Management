package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "maintenancechecklistitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceChecklistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;
    
    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord maintenanceRecord;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = true)
    private Part part;
    
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    
    @Column(name = "item_description", length = 255)
    private String itemDescription;
    
    @Column(name = "labor_cost", precision = 18, scale = 2)
    private BigDecimal laborCost;

    @Column(name = "part_quantity")
    private Integer partQuantity;

    @Column(name = "is_completed")
    private Boolean isCompleted;
}
