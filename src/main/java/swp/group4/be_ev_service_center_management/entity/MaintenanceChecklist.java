package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenancechecklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceChecklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Integer checklistId;
    
    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord maintenanceRecord;
    
    @Column(name = "summary", length = 500)
    private String summary;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
