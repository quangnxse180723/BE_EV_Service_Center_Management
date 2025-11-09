package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.PackageChecklistItemDTO;
import java.util.List;

public interface PackageChecklistItemService {
    List<PackageChecklistItemDTO> getAllPackageChecklistItems();
    PackageChecklistItemDTO getPackageChecklistItemById(Integer itemId);
}
