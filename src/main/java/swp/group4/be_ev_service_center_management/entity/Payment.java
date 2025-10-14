package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;
    
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @Column(name = "method", length = 20)
    private String method; // BANKING, CASH, EWALLET
    
    @CreationTimestamp
    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;
    
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
}
