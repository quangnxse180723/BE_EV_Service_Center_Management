package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByServiceCenter_CenterIdAndDateAndStatus(Integer centerId, LocalDate date, String status);
}