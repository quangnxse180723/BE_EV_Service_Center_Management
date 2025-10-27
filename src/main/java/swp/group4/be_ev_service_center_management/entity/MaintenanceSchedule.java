package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "maintenanceschedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private ServiceCenter serviceCenter;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // Cách 1: Dùng @ManyToOne (có thể null nếu slot không tồn tại)
    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = true, insertable = false, updatable = false)
    private TimeSlot timeSlot;
    
    // Cách 2: Thêm field Integer để lưu slotId trực tiếp
    @Column(name = "slot_id")
    private Integer slotId;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;
    
    // ✅ ĐÚNG - Lưu LocalTime không timezone
    @Column(name = "scheduled_time", columnDefinition = "TIME")
    private LocalTime scheduledTime; // Lưu giờ thuần: 16:00:00, KHÔNG convert timezone
    
    @ManyToOne
    @JoinColumn(name = "package_id")
    private MaintenancePackage maintenancePackage;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, CONFIRMED, IN_PROGRESS, DONE, CANCELLED
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

}
