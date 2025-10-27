package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.CenterMembersResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.CenterService;

@RestController
@RequestMapping("/api/center")
@RequiredArgsConstructor
public class CenterController {
    private final CenterService centerService;

    @GetMapping("/{centerId}/members")
    public ResponseEntity<CenterMembersResponse> getMembersByCenter(@PathVariable Integer centerId) {
        return ResponseEntity.ok(centerService.getMembersByCenterId(centerId));
    }
}
