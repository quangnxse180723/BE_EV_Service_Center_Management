package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.StaffRequest;
import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailForStaffResponse;
import swp.group4.be_ev_service_center_management.dto.response.StaffResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.PaymentService;
import swp.group4.be_ev_service_center_management.service.interfaces.StaffService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final PaymentService paymentService;

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

    /**
     * GET /api/staff/payments/{scheduleId}
     * Lấy chi tiết invoice + biên bản sửa chữa
     */
    @GetMapping("/payments/{scheduleId}")
    public ResponseEntity<InvoiceDetailForStaffResponse> getInvoiceDetail(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(paymentService.getInvoiceDetailByScheduleId(scheduleId));
    }

    /**
     * POST /api/staff/payments/send-invoice
     * Gửi hóa đơn cho khách hàng
     * Body: { "scheduleId": 123, "paymentMethod": "BANK" }
     */
    @PostMapping("/payments/send-invoice")
    public ResponseEntity<Map<String, String>> sendInvoiceToCustomer(@RequestBody Map<String, Object> body) {
        Integer scheduleId = (Integer) body.get("scheduleId");
        String paymentMethod = (String) body.get("paymentMethod");
        
        paymentService.sendInvoiceToCustomer(scheduleId, paymentMethod);
        
        return ResponseEntity.ok(Map.of("message", "Đã gửi hóa đơn cho khách hàng thành công"));
    }

    /**
     * POST /api/staff/payments/customer-approve
     * Customer duyệt/chỉnh sửa checklist (bỏ tick items không cần)
     * Body: { "scheduleId": 123, "approvedItemIds": [1, 2, 3] }
     */
    @PostMapping("/payments/customer-approve")
    public ResponseEntity<Map<String, String>> customerApproveChecklist(@RequestBody Map<String, Object> body) {
        Integer scheduleId = (Integer) body.get("scheduleId");
        @SuppressWarnings("unchecked")
        List<Integer> approvedItemIds = (List<Integer>) body.get("approvedItemIds");
        
        paymentService.customerApproveChecklist(scheduleId, approvedItemIds);
        
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật biên bản thành công"));
    }
}