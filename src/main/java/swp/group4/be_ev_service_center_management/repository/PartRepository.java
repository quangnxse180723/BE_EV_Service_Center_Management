package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.Part;
import java.util.List;

public interface PartRepository extends JpaRepository<Part, Integer> {
    List<Part> findByServiceCenterCenterId(Integer centerId);
}