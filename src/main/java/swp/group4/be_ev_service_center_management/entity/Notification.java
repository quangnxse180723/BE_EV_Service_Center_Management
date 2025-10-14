package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;
    
    @ManyToOne
    @JoinColumn(name = "related_record_id")
    private MaintenanceRecord relatedRecord;
    
    @Column(name = "type", nullable = false, length = 30)
    private String type;
    
    @Column(name = "title", length = 255)
    private String title;
    
    @Column(name = "message", length = 1000)
    private String message;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
}
