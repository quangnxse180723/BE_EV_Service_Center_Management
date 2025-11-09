package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.PackageItemDTO;
import swp.group4.be_ev_service_center_management.repository.PackageChecklistItemRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.PackageItemService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageItemImpl implements PackageItemService {
    private final PackageChecklistItemRepository repository;

    public List<PackageItemDTO> getAllPackageItems() {
        List<Object[]> results = repository.findAllPackageItemsRaw();
        return results.stream().map(row -> new PackageItemDTO(
                ((Number) row[0]).intValue(),
                (String) row[1],
                ((Number) row[2]).intValue(),
                (String) row[3],
                (String) row[4],
                (java.math.BigDecimal) row[5]
        )).collect(Collectors.toList());
    }
}
