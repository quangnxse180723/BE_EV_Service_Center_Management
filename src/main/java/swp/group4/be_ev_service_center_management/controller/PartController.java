package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.PartRequest;
import swp.group4.be_ev_service_center_management.dto.response.PartResponse;
import swp.group4.be_ev_service_center_management.service.PartService;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {
    private final PartService partService;

    @PostMapping
    public ResponseEntity<PartResponse> add(@RequestBody PartRequest req) {
        return ResponseEntity.ok(partService.addPart(req));
    }

    @PostMapping("/{partId}/restock")
    public ResponseEntity<Void> restock(@PathVariable Integer partId, @RequestParam Integer quantity) {
        partService.restock(partId, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/center/{centerId}")
    public ResponseEntity<List<PartResponse>> getByCenter(@PathVariable Integer centerId) {
        return ResponseEntity.ok(partService.getPartsByCenter(centerId));
    }
}