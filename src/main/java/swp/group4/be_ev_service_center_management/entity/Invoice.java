package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;
    
    @OneToOne
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MaintenanceRecord maintenanceRecord;
    
    @Column(name = "total_labor_cost", precision = 18, scale = 2)
    private BigDecimal totalLaborCost = BigDecimal.ZERO;
    
    @Column(name = "total_part_cost", precision = 18, scale = 2)
    private BigDecimal totalPartCost = BigDecimal.ZERO;
    
    // Calculated field: total_amount = total_labor_cost + total_part_cost
    // Sử dụng @Formula để tính toán tự động
    @Formula("(total_labor_cost + total_part_cost)")
    @Column(name = "total_amount", precision = 18, scale = 2, insertable = false, updatable = false)
    private BigDecimal totalAmount;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", length = 20)
    private String status = "UNPAID"; // UNPAID, PAID
}
