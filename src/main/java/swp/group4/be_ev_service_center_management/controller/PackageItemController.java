package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.PackageItemDTO;
import swp.group4.be_ev_service_center_management.service.interfaces.PackageItemService;
import java.util.List;

@RestController
@RequestMapping("/api/package-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PackageItemController {
    private final PackageItemService service;

    @GetMapping
    public List<PackageItemDTO> getAllPackageItems() {
        return service.getAllPackageItems();
    }
}
