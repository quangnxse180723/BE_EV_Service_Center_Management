package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "maintenanceitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;
    
    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private MaintenanceChecklist maintenanceChecklist;
    
    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "labor_cost", precision = 18, scale = 2)
    private BigDecimal laborCost;
    
    @Column(name = "part_cost", precision = 18, scale = 2)
    private BigDecimal partCost;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, CUSTOMER_APPROVED, DONE, REJECTED
}
