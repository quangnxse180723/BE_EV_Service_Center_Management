package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.PartDTO;
import swp.group4.be_ev_service_center_management.service.PartService;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PartController {
    
    private final PartService partService;

    /**
     * GET /api/parts
     * Lấy tất cả parts
     */
    @GetMapping
    public ResponseEntity<List<PartDTO>> getAllParts() {
        List<PartDTO> parts = partService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    /**
     * GET /api/parts/{partId}
     * Lấy part theo ID
     */
    @GetMapping("/{partId}")
    public ResponseEntity<PartDTO> getPartById(@PathVariable Integer partId) {
        PartDTO part = partService.getPartById(partId);
        return ResponseEntity.ok(part);
    }
}
