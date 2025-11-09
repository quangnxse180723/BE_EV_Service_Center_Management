package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.PackageChecklistItemDTO;
import swp.group4.be_ev_service_center_management.entity.PackageChecklistItem;
import swp.group4.be_ev_service_center_management.repository.PackageChecklistItemRepository;
import swp.group4.be_ev_service_center_management.service.PackageChecklistItemService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageChecklistItemServiceImpl implements PackageChecklistItemService {
    
    private final PackageChecklistItemRepository packageChecklistItemRepository;

    @Override
    public List<PackageChecklistItemDTO> getAllPackageChecklistItems() {
        List<PackageChecklistItem> items = packageChecklistItemRepository.findAll();
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PackageChecklistItemDTO getPackageChecklistItemById(Integer itemId) {
        PackageChecklistItem item = packageChecklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("PackageChecklistItem not found with id: " + itemId));
        return convertToDTO(item);
    }

    private PackageChecklistItemDTO convertToDTO(PackageChecklistItem item) {
        PackageChecklistItemDTO dto = new PackageChecklistItemDTO();
        dto.setChecklistItemId(item.getItemId()); // itemId -> checklistItemId
        dto.setPackageId(item.getMaintenancePackage() != null ? item.getMaintenancePackage().getPackageId() : null);
        dto.setPartId(item.getPart() != null ? item.getPart().getPartId() : null);
        dto.setItemName(item.getItemName());
        dto.setItemDescription(item.getItemDescription());
        dto.setLaborCost(item.getDefaultLaborCost()); // defaultLaborCost -> laborCost
        return dto;
    }
}
