package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "vin", unique = true, length = 50)
    private String vin;
    
    @Column(name = "license_plate", length = 20)
    private String licensePlate;
    
    @Column(name = "current_mileage")
    private Integer currentMileage;
    
    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;
}
