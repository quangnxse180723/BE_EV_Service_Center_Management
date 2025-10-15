package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    // Tìm customer theo account
    Optional<Customer> findByAccount_AccountId(Integer accountId);
    
    // Tìm customer theo email
    Optional<Customer> findByEmail(String email);
    
    // Tìm customer theo phone
    Optional<Customer> findByPhone(String phone);
    
    // Tìm customer theo tên (contains)
    List<Customer> findByFullNameContainingIgnoreCase(String name);
}
