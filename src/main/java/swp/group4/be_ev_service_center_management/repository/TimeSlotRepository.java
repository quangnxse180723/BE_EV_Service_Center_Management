package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.TimeSlot;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    
    // Tìm time slots theo service center
    List<TimeSlot> findByServiceCenter_CenterId(Integer centerId);
    
    // Tìm time slots available
    List<TimeSlot> findByServiceCenter_CenterIdAndIsAvailable(Integer centerId, Boolean isAvailable);
}
