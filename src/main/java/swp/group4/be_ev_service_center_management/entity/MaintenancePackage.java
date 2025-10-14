package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maintenancepackage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "mileage_milestone")
    private Integer mileageMilestone;
    
    @Column(name = "description", length = 500)
    private String description;
}
