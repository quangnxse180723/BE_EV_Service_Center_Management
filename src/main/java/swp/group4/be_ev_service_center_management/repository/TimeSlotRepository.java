package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByServiceCenter_CenterIdAndDateAndStatus(Integer centerId, LocalDate date, String status);
    
    Optional<TimeSlot> findByServiceCenter_CenterIdAndDateAndStartTime(
            Integer centerId,
            LocalDate date,
            LocalTime startTime
    );
}