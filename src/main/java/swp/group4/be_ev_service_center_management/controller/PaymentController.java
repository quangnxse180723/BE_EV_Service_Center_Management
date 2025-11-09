package swp.group4.be_ev_service_center_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.response.RevenueResponse;
import swp.group4.be_ev_service_center_management.service.interfaces.PaymentService;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/management")
    public ResponseEntity<List<PaymentManagementResponse>> getAllPaymentsForManagement() {
        return ResponseEntity.ok(paymentService.getAllPaymentsForManagement());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentManagementResponse>> getCustomerPaymentHistory(@PathVariable int customerId) {
        return ResponseEntity.ok(paymentService.getCustomerPaymentHistory(customerId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentManagementResponse> getPaymentById(@PathVariable int paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> body) {
        // build PaymentRequest manually to avoid compile-time dependency
        Map<String, Object> map = body == null ? new HashMap<>() : body;
        swp.group4.be_ev_service_center_management.dto.request.PaymentRequest req = new swp.group4.be_ev_service_center_management.dto.request.PaymentRequest();
        if (map.containsKey("amount")) req.setAmount(new BigDecimal(map.get("amount").toString()));
        if (map.containsKey("method")) req.setMethod(map.get("method").toString());
        if (map.containsKey("invoiceId")) req.setInvoiceId((Integer) map.get("invoiceId"));
        if (map.containsKey("transactionReference")) req.setTransactionReference((String) map.get("transactionReference"));
        return ResponseEntity.ok(paymentService.createPayment(req));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable int paymentId,
            @RequestBody Map<String, Object> body) {
        String status = body != null && body.containsKey("status") ? String.valueOf(body.get("status")) : null;
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, status));
    }

    @GetMapping("/statistics/{customerId}/{year}")
    public ResponseEntity<?> getPaymentStatistics(
            @PathVariable int customerId,
            @PathVariable int year) {
        return ResponseEntity.ok(paymentService.getPaymentStatistics(customerId, year));
    }

    @PutMapping("/{invoiceId}/pay")
    public ResponseEntity<?> payInvoice(
            @PathVariable int invoiceId,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> map = body == null ? new HashMap<>() : body;
        swp.group4.be_ev_service_center_management.dto.request.PaymentRequest req = new swp.group4.be_ev_service_center_management.dto.request.PaymentRequest();
        if (map.containsKey("amount")) req.setAmount(new BigDecimal(map.get("amount").toString()));
        if (map.containsKey("method")) req.setMethod(map.get("method").toString());
        if (map.containsKey("transactionReference")) req.setTransactionReference((String) map.get("transactionReference"));
        return ResponseEntity.ok(paymentService.payInvoice(invoiceId, req));
    }
    
    /**
     * GET /api/payments/revenue
     * Lấy tổng doanh thu theo ngày/tuần/tháng/năm
     * @param date - Ngày cụ thể (format: YYYY-MM-DD)
     * @param type - Loại thống kê: day, week, month, year
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueResponse> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String type) {
        return ResponseEntity.ok(paymentService.calculateRevenue(date, type));
    }
}
