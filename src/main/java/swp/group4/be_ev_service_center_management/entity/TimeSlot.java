package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "timeslot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Integer slotId;
    
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private ServiceCenter serviceCenter;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "status", length = 15)
    private String status = "AVAILABLE"; // AVAILABLE, BOOKED
}
