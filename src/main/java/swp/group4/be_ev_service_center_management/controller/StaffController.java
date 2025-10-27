package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.StaffRequest;
import swp.group4.be_ev_service_center_management.dto.response.StaffResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.StaffService;

import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaffs() {
        return ResponseEntity.ok(staffService.getAllStaffs());
    }

    @PostMapping
    public ResponseEntity<StaffResponse> addStaff(@RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.addStaff(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable("id") Integer id, @RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.updateStaff(id, request));
    }

    // API lấy chi tiết hóa đơn theo scheduleId
    @GetMapping("/invoice-detail/{scheduleId}")
    public ResponseEntity<InvoiceDetailResponse> getInvoiceDetail(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(invoiceService.getInvoiceDetailByScheduleId(scheduleId));
    }
}

