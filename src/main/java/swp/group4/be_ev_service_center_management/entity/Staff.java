package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;
    
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private ServiceCenter serviceCenter;
    
    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
