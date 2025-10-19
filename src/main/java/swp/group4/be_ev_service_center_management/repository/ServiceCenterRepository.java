package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;

import java.util.List;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Integer> {
    
    // Tìm trung tâm bảo dưỡng theo tên
    @Query("SELECT sc FROM ServiceCenter sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ServiceCenter> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Tìm trung tâm bảo dưỡng theo địa chỉ
    @Query("SELECT sc FROM ServiceCenter sc WHERE LOWER(sc.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<ServiceCenter> findByAddressContainingIgnoreCase(@Param("address") String address);
    
    // Tìm trung tâm bảo dưỡng theo đánh giá từ cao đến thấp
    @Query("SELECT sc FROM ServiceCenter sc ORDER BY sc.rating DESC")
    List<ServiceCenter> findAllOrderByRatingDesc();
    
    // Tìm trung tâm bảo dưỡng theo khoảng cách (giả sử có trường latitude và longitude)
    @Query(value = "SELECT * FROM service_center " +
           "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(latitude)))) ASC", nativeQuery = true)
    List<ServiceCenter> findNearestServiceCenters(@Param("latitude") double latitude, 
                                                @Param("longitude") double longitude);

    // Kiểm tra trung tâm có còn hoạt động không
    @Query("SELECT sc FROM ServiceCenter sc WHERE sc.serviceCenterId = :id AND sc.status = 'ACTIVE'")
    ServiceCenter findActiveServiceCenterById(@Param("id") Integer id);
}