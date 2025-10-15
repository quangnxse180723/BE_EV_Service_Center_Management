package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenancerecord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name="center_id")
    private ServiceCenter serviceCenter;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private MaintenanceSchedule maintenanceSchedule;
    
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;
    
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "mileage_at_service")
    private Integer mileageAtService;
    
    @Column(name = "status", nullable = false, length = 30)
    private String status; // IN_PROGRESS, WAITING_APPROVE, CUSTOMER_APPROVED, COMPLETED
    
    @Column(name = "total_cost", precision = 18, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "note", length = 500)
    private String note;
}
