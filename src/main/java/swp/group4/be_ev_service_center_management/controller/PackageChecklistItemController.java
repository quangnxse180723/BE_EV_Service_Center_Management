package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.PackageChecklistItemDTO;
import swp.group4.be_ev_service_center_management.service.PackageChecklistItemService;
import java.util.List;

@RestController
@RequestMapping("/api/package-checklist-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PackageChecklistItemController {
    
    private final PackageChecklistItemService packageChecklistItemService;

    /**
     * GET /api/package-checklist-items
     * Lấy tất cả package checklist items
     */
    @GetMapping
    public ResponseEntity<List<PackageChecklistItemDTO>> getAllPackageChecklistItems() {
        List<PackageChecklistItemDTO> items = packageChecklistItemService.getAllPackageChecklistItems();
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/package-checklist-items/{itemId}
     * Lấy package checklist item theo ID
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<PackageChecklistItemDTO> getPackageChecklistItemById(@PathVariable Integer itemId) {
        PackageChecklistItemDTO item = packageChecklistItemService.getPackageChecklistItemById(itemId);
        return ResponseEntity.ok(item);
    }
}
