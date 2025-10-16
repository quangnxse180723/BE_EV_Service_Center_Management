package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Technician;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Integer> {
}
