package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    // Tìm customer theo email
    Optional<Customer> findByEmail(String email);
    
    // Tìm customer theo số điện thoại
    Optional<Customer> findByPhone(String phone);
    
    // Tìm customer theo tên (LIKE search)
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> findByFullNameContaining(@Param("name") String name);
    
    // Tìm customer theo email hoặc số điện thoại
    @Query("SELECT c FROM Customer c WHERE c.email = :identifier OR c.phone = :identifier")
    Optional<Customer> findByEmailOrPhone(@Param("identifier") String identifier);
    
    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
    
    // Kiểm tra phone đã tồn tại
    boolean existsByPhone(String phone);
    
    // Lấy tất cả customers có xe (có lịch sử bảo dưỡng)
    @Query("SELECT DISTINCT c FROM Customer c INNER JOIN Vehicle v ON v.customer.customerId = c.customerId")
    List<Customer> findCustomersWithVehicles();
}
