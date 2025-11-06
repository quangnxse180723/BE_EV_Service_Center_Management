package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    // VNPay config - thay bằng thông tin thực tế từ VNPay
    private static final String VNP_TMN_CODE = "ULIOG3X9"; // Mã website tại VNPay
    private static final String VNP_HASH_SECRET = "83GZXC9B00GQPIVQYT20907PRCIHSALP"; // Chuỗi bí mật
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // URL thanh toán VNPay
    private static final String VNP_RETURN_URL = "http://localhost:5173/customer/payment/vnpay-return"; // URL return sau khi thanh toán

    /**
     * POST /api/payment/vnpay/create
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Map<String, Object> requestData) 
            throws UnsupportedEncodingException {
        
        try {
            System.out.println("VNPay create payment request: " + requestData);
            
            // Parse scheduleId an toàn (có thể là String hoặc Integer)
            Integer scheduleId = null;
            Object scheduleIdObj = requestData.get("scheduleId");
            if (scheduleIdObj instanceof String) {
                scheduleId = Integer.parseInt((String) scheduleIdObj);
            } else if (scheduleIdObj instanceof Number) {
                scheduleId = ((Number) scheduleIdObj).intValue();
            }
            
            // Parse amount an toàn
            Long amount = null;
            Object amountObj = requestData.get("amount");
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).longValue();
            } else if (amountObj instanceof String) {
                amount = Long.parseLong((String) amountObj);
            }
            
            String orderInfo = (String) requestData.get("orderInfo");
            
            System.out.println("Parsed - scheduleId: " + scheduleId + ", amount: " + amount + ", orderInfo: " + orderInfo);
            
            // Validate
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Invalid amount: " + amount);
            }

            // Tạo mã đơn hàng unique
            String vnp_TxnRef = "ORDER" + System.currentTimeMillis();
            String vnp_IpAddr = "127.0.0.1"; // IP address của khách hàng
            
            // Convert amount to VNPay format (x100)
            long vnp_Amount = amount * 100;

        // Tạo các tham số cho VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Thêm thời gian tạo và hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build query string và hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNP_URL + "?" + queryUrl;

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        response.put("txnRef", vnp_TxnRef);
        
        System.out.println("VNPay payment URL created: " + paymentUrl);
        
        return ResponseEntity.ok(response);
        
        } catch (Exception e) {
            System.err.println("Error creating VNPay payment: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * GET /api/payment/vnpay/return
     * Callback từ VNPay sau khi khách hàng thanh toán
     */
    @GetMapping("/return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_Amount = params.get("vnp_Amount");
        
        Map<String, Object> response = new HashMap<>();
        
        if ("00".equals(vnp_ResponseCode)) {
            // Thanh toán thành công
            response.put("success", true);
            response.put("message", "Thanh toan thanh cong");
            response.put("txnRef", vnp_TxnRef);
            response.put("amount", Long.parseLong(vnp_Amount) / 100);
            
            // TODO: Cập nhật database - đánh dấu invoice đã thanh toán
            // paymentService.updatePaymentStatus(scheduleId, "PAID");
            
        } else {
            // Thanh toán thất bại
            response.put("success", false);
            response.put("message", "Thanh toan that bai");
            response.put("responseCode", vnp_ResponseCode);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * HMAC SHA512 để tạo secure hash
     */
    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), 
                    "HmacSHA512"
            );
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
