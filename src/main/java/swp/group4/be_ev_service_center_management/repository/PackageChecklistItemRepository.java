package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import swp.group4.be_ev_service_center_management.entity.MaintenancePackage;
import swp.group4.be_ev_service_center_management.entity.PackageChecklistItem;
import swp.group4.be_ev_service_center_management.dto.PackageItemDTO;
import java.util.List;

public interface PackageChecklistItemRepository extends JpaRepository<PackageChecklistItem, Integer> {

    @Query(value = """
        SELECT 
            p.package_id AS packageId,
            p.name AS packageName,
            i.item_id AS itemId,
            i.item_name AS itemName,
            i.item_description AS itemDescription,
            i.default_labor_cost AS defaultLaborCost
        FROM maintenancepackage p
        CROSS JOIN packagechecklistitem i
        """, nativeQuery = true)
    List<Object[]> findAllPackageItemsRaw();

    List<PackageChecklistItem> findByMaintenancePackage(MaintenancePackage maintenancePackage);
}
