package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Part;

import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Integer> {
    Optional<Part> findByName(String name);
}
