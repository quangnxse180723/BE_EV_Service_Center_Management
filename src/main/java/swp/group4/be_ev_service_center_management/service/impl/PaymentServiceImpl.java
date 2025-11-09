package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailForStaffResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentStatisticsResponse;
import swp.group4.be_ev_service_center_management.dto.response.RevenueResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;
import swp.group4.be_ev_service_center_management.service.interfaces.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final MaintenanceChecklistItemRepository maintenanceChecklistItemRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final NotificationService notificationService;

    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public List<PaymentManagementResponse> getAllPaymentsForManagement() {
        List<Invoice> invoices = invoiceRepository.findAll();
        
        // Chỉ lấy invoice có schedule.status = "COMPLETED"
        return invoices.stream()
                .filter(invoice -> {
                    if (invoice.getMaintenanceRecord() != null 
                        && invoice.getMaintenanceRecord().getMaintenanceSchedule() != null) {
                        String status = invoice.getMaintenanceRecord().getMaintenanceSchedule().getStatus();
                        return "COMPLETED".equalsIgnoreCase(status);
                    }
                    return false;
                })
                .map(this::mapInvoiceToPaymentManagementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentManagementResponse> getCustomerPaymentHistory(int customerId) {
        List<Payment> payments = paymentRepository.findByInvoice_MaintenanceRecord_MaintenanceSchedule_Customer_CustomerId(customerId);
        
        // ✅ Lọc duplicate: Chỉ lấy payment MỚI NHẤT của mỗi invoice
        Map<Integer, Payment> latestPaymentPerInvoice = new HashMap<>();
        for (Payment payment : payments) {
            Integer invoiceId = payment.getInvoice().getInvoiceId();
            Payment existing = latestPaymentPerInvoice.get(invoiceId);
            
            // Lấy payment có paymentDate mới nhất, hoặc paymentId lớn nhất nếu cùng ngày
            if (existing == null || 
                (payment.getPaymentDate() != null && 
                 (existing.getPaymentDate() == null || 
                  payment.getPaymentDate().isAfter(existing.getPaymentDate())))) {
                latestPaymentPerInvoice.put(invoiceId, payment);
            }
        }
        
        return latestPaymentPerInvoice.values().stream()
                .map(this::mapPaymentToPaymentManagementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentManagementResponse getPaymentById(int paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        return mapPaymentToPaymentManagementResponse(payment);
    }

    @Override
    @Transactional
    public PaymentManagementResponse createPayment(PaymentRequest paymentRequest) {
        Integer invoiceId = paymentRequest.getInvoiceId();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentRequest.getAmount() == null ? BigDecimal.ZERO : paymentRequest.getAmount());
        payment.setMethod(paymentRequest.getMethod());
        payment.setTransactionReference(paymentRequest.getTransactionReference());
        Payment saved = paymentRepository.save(payment);

        // Optionally update invoice status when creating a payment (not auto-paid)
        // Do not change invoice.status here unless business requires

        return mapPaymentToPaymentManagementResponse(saved);
    }

    @Override
    @Transactional
    public PaymentManagementResponse updatePaymentStatus(int paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        Invoice invoice = payment.getInvoice();
        if (invoice == null) throw new RuntimeException("Invoice not linked to payment id: " + paymentId);
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
        return mapPaymentToPaymentManagementResponse(payment);
    }

    @Override
    public PaymentStatisticsResponse getPaymentStatistics(int customerId, int year) {
        List<Payment> payments = paymentRepository.findByInvoice_MaintenanceRecord_MaintenanceSchedule_Customer_CustomerId(customerId);
        BigDecimal total = payments.stream()
                .filter(p -> p.getPaymentDate() != null && p.getPaymentDate().getYear() == year)
                .map(Payment::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PaymentStatisticsResponse resp = new PaymentStatisticsResponse();
        resp.setTotalAmount(total);
        resp.setYear(year);
        return resp;
    }

    @Override
    @Transactional
    public PaymentManagementResponse payInvoice(int invoiceId, PaymentRequest paymentRequest) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentRequest.getAmount() == null ? invoice.getTotalAmount() : paymentRequest.getAmount());
        payment.setMethod(paymentRequest.getMethod());
        payment.setTransactionReference(paymentRequest.getTransactionReference());
        Payment saved = paymentRepository.save(payment);

        // mark invoice as PAID
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        return mapPaymentToPaymentManagementResponse(saved);
    }

    // Helpers
    private PaymentManagementResponse mapInvoiceToPaymentManagementResponse(Invoice invoice) {
        PaymentManagementResponse.PaymentManagementResponseBuilder builder = PaymentManagementResponse.builder();
        if (invoice.getMaintenanceRecord() != null && invoice.getMaintenanceRecord().getMaintenanceSchedule() != null) {
            MaintenanceSchedule schedule = invoice.getMaintenanceRecord().getMaintenanceSchedule();
            builder.scheduleId(schedule.getScheduleId()); // Thêm scheduleId
            if (schedule.getCustomer() != null) builder.customerName(schedule.getCustomer().getFullName());
            if (schedule.getVehicle() != null) builder.vehicleName(schedule.getVehicle().getModel()).licensePlate(schedule.getVehicle().getLicensePlate());
            if (schedule.getScheduledDate() != null) builder.scheduledDate(schedule.getScheduledDate().format(SDF));
        }
        builder.status(mapInvoiceStatus(invoice.getStatus()));
        builder.action(mapAction(invoice.getStatus()));
        return builder.build();
    }

    private PaymentManagementResponse mapPaymentToPaymentManagementResponse(Payment payment) {
        PaymentManagementResponse.PaymentManagementResponseBuilder builder = PaymentManagementResponse.builder();
        if (payment.getInvoice() != null && payment.getInvoice().getMaintenanceRecord() != null && payment.getInvoice().getMaintenanceRecord().getMaintenanceSchedule() != null) {
            MaintenanceSchedule schedule = payment.getInvoice().getMaintenanceRecord().getMaintenanceSchedule();
            builder.scheduleId(schedule.getScheduleId()); // Thêm scheduleId
            if (schedule.getCustomer() != null) builder.customerName(schedule.getCustomer().getFullName());
            if (schedule.getVehicle() != null) builder.vehicleName(schedule.getVehicle().getModel()).licensePlate(schedule.getVehicle().getLicensePlate());
            if (schedule.getScheduledDate() != null) builder.scheduledDate(schedule.getScheduledDate().format(SDF));
        }
        String invoiceStatus = payment.getInvoice() != null ? payment.getInvoice().getStatus() : null;
        builder.status(mapInvoiceStatus(invoiceStatus));
        builder.action(mapAction(invoiceStatus));
        return builder.build();
    }

    private String mapInvoiceStatus(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "PAID", "ĐÃ_THANH_TOÁN" -> "Đã thanh toán";
            case "UNPAID", "CHỜ_THANH_TOÁN" -> "Chờ thanh toán";
            default -> status;
        };
    }

    private String mapAction(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "PAID", "ĐÃ_THANH_TOÁN" -> "Xem hóa đơn";
            case "UNPAID", "CHỜ_THANH_TOÁN" -> "In hóa đơn";
            default -> "";
        };
    }

    /**
     * Lấy chi tiết Invoice + Biên bản sửa chữa theo scheduleId cho Staff
     */
    @Override
    public InvoiceDetailForStaffResponse getInvoiceDetailByScheduleId(Integer scheduleId) {
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + scheduleId));

        // Tìm MaintenanceRecord qua schedule
        List<MaintenanceRecord> records = maintenanceRecordRepository.findByMaintenanceSchedule(schedule);
        if (records.isEmpty()) {
            throw new RuntimeException("Lịch bảo dưỡng chưa có biên bản sửa chữa");
        }
        MaintenanceRecord record = records.get(0); // Lấy record đầu tiên

        Invoice invoice = invoiceRepository.findByMaintenanceRecord(record)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn cho lịch này"));

        // Lấy danh sách items từ checklist
        List<MaintenanceChecklistItem> items = maintenanceChecklistItemRepository
                .findByMaintenanceRecord_RecordId(record.getRecordId());

        List<InvoiceDetailForStaffResponse.MaintenanceItemDetail> itemDetails = items.stream()
                .map(item -> {
                    BigDecimal partCost = BigDecimal.ZERO;
                    if (item.getPart() != null && item.getPartQuantity() != null) {
                        partCost = item.getPart().getPrice().multiply(new BigDecimal(item.getPartQuantity()));
                    }
                    
                    return InvoiceDetailForStaffResponse.MaintenanceItemDetail.builder()
                            .itemId(item.getItemId())
                            .itemName(item.getItemName())
                            .actionStatus(item.getIsCompleted() != null && item.getIsCompleted() ? "Completed" : "Pending")
                            .laborCost(item.getLaborCost() != null ? item.getLaborCost() : BigDecimal.ZERO)
                            .partCost(partCost)
                            .mileage(null) // MaintenanceChecklistItem không có mileage
                            .months(null) // MaintenanceChecklistItem không có months
                            .build();
                })
                .collect(Collectors.toList());

        // Tổng chi phí
        BigDecimal totalCost = invoice.getTotalLaborCost().add(invoice.getTotalPartCost());

        // Lấy payment method nếu đã có payment
        String paymentMethod = null;
        String paymentStatus = "Chờ thanh toán";
        List<Payment> payments = paymentRepository.findByInvoice_InvoiceId(invoice.getInvoiceId());
        if (!payments.isEmpty()) {
            Payment lastPayment = payments.get(payments.size() - 1);
            paymentMethod = lastPayment.getMethod(); // BANK / CASH
            if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
                paymentStatus = paymentMethod != null ? 
                    ("BANK".equalsIgnoreCase(paymentMethod) ? "Ngân hàng" : "Tiền mặt") : 
                    "Đã thanh toán";
            }
        }

        return InvoiceDetailForStaffResponse.builder()
                .customerName(schedule.getCustomer() != null ? schedule.getCustomer().getFullName() : "N/A")
                .vehicleName(schedule.getVehicle() != null ? schedule.getVehicle().getModel() : "N/A")
                .licensePlate(schedule.getVehicle() != null ? schedule.getVehicle().getLicensePlate() : "N/A")
                .technicianName(schedule.getTechnician() != null ? 
                    schedule.getTechnician().getAccount().getFullName() : "N/A")
                .inspectionDate(LocalDate.now()) // ✅ Ngày in hóa đơn = ngày hiện tại (khi staff gửi)
                .status(mapInvoiceStatus(invoice.getStatus()))
                .items(itemDetails)
                .totalCost(totalCost)
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .scheduleId(scheduleId)
                .invoiceId(invoice.getInvoiceId())
                .customerId(schedule.getCustomer() != null ? schedule.getCustomer().getCustomerId() : null)
                .build();
    }

    /**
     * Staff gửi hóa đơn cho khách hàng (tạo notification)
     */
    @Override
    @Transactional
    public void sendInvoiceToCustomer(Integer scheduleId, String paymentMethod) {
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + scheduleId));

        // Tìm MaintenanceRecord qua schedule
        List<MaintenanceRecord> records = maintenanceRecordRepository.findByMaintenanceSchedule(schedule);
        if (records.isEmpty()) {
            throw new RuntimeException("Lịch bảo dưỡng chưa có biên bản sửa chữa");
        }
        MaintenanceRecord record = records.get(0);

        Invoice invoice = invoiceRepository.findByMaintenanceRecord(record)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn cho lịch này"));

        // Tạo hoặc cập nhật payment
        List<Payment> existingPayments = paymentRepository.findByInvoice_InvoiceId(invoice.getInvoiceId());
        Payment payment;
        if (existingPayments.isEmpty()) {
            payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(invoice.getTotalLaborCost().add(invoice.getTotalPartCost()));
        } else {
            payment = existingPayments.get(0);
        }
        payment.setMethod(paymentMethod);
        payment.setTransactionReference("PENDING_CUSTOMER_PAYMENT");
        paymentRepository.save(payment);

        // Cập nhật invoice status
        invoice.setStatus("PENDING_PAYMENT");
        invoiceRepository.save(invoice);

        // Gửi notification cho khách hàng
        Customer customer = schedule.getCustomer();
        if (customer != null && customer.getAccount() != null) {
            String vehicleInfo = schedule.getVehicle() != null ? schedule.getVehicle().getModel() : "Xe";
            String technicianName = schedule.getTechnician() != null && schedule.getTechnician().getAccount() != null 
                    ? schedule.getTechnician().getAccount().getFullName() 
                    : "N/A";
            String scheduledDate = schedule.getScheduledDate() != null 
                    ? schedule.getScheduledDate().toLocalDate().toString() 
                    : "N/A";
            BigDecimal totalCost = invoice.getTotalLaborCost() != null && invoice.getTotalPartCost() != null
                    ? invoice.getTotalLaborCost().add(invoice.getTotalPartCost())
                    : BigDecimal.ZERO;
            String paymentMethodText = paymentMethod != null 
                    ? (paymentMethod.equals("BANK") ? "Ngân hàng" : "Tiền mặt") 
                    : "Chưa chọn";
            
            String message = String.format(
                    "Biên bản kiểm tra %s - %s đã hoàn thành. Vui lòng xem và phê duyệt. Tổng chi phí dự kiến: %,.0f VND",
                    vehicleInfo,
                    scheduledDate,
                    totalCost
            );

            String link = "/customer/payment/" + scheduleId;

            notificationService.createNotification(
                    customer.getAccount().getAccountId(),
                    message,
                    link
            );
        }
    }

    /**
     * Customer duyệt/chỉnh sửa checklist (bỏ tick các items không cần)
     * Gửi notification cho technician
     */
    @Override
    @Transactional
    public void customerApproveChecklist(Integer scheduleId, List<Integer> approvedItemIds) {
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + scheduleId));

        List<MaintenanceRecord> records = maintenanceRecordRepository.findByMaintenanceSchedule(schedule);
        if (records.isEmpty()) {
            throw new RuntimeException("Lịch bảo dưỡng chưa có biên bản sửa chữa");
        }
        MaintenanceRecord record = records.get(0);

        // Lấy tất cả items
        List<MaintenanceChecklistItem> allItems = maintenanceChecklistItemRepository
                .findByMaintenanceRecord_RecordId(record.getRecordId());

        // Cập nhật trạng thái: approved items = completed, không approved = bỏ đi
        int removedCount = 0;
        for (MaintenanceChecklistItem item : allItems) {
            if (approvedItemIds.contains(item.getItemId())) {
                item.setIsCompleted(true); // Customer duyệt
            } else {
                // Customer bỏ tick -> xóa item này
                maintenanceChecklistItemRepository.delete(item);
                removedCount++;
            }
        }
        
        // Save approved items
        List<MaintenanceChecklistItem> approvedItems = allItems.stream()
                .filter(item -> approvedItemIds.contains(item.getItemId()))
                .collect(Collectors.toList());
        maintenanceChecklistItemRepository.saveAll(approvedItems);

        // Cập nhật lại tổng chi phí trong invoice
        Invoice invoice = invoiceRepository.findByMaintenanceRecord(record)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        BigDecimal newTotalLabor = approvedItems.stream()
                .map(item -> item.getLaborCost() != null ? item.getLaborCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalPart = approvedItems.stream()
                .map(item -> {
                    if (item.getPart() != null && item.getPartQuantity() != null) {
                        return item.getPart().getPrice().multiply(new BigDecimal(item.getPartQuantity()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalLaborCost(newTotalLabor);
        invoice.setTotalPartCost(newTotalPart);
        invoiceRepository.save(invoice);

        // Gửi notification cho technician
        if (schedule.getTechnician() != null && schedule.getTechnician().getAccount() != null) {
            String vehicleInfo = schedule.getVehicle() != null ? 
                    schedule.getVehicle().getModel() + " - " + schedule.getVehicle().getLicensePlate() : "Xe";
            String customerName = schedule.getCustomer() != null ? schedule.getCustomer().getFullName() : "Khách hàng";
            BigDecimal totalCost = newTotalLabor.add(newTotalPart);

            String message = String.format(
                    "%s đã duyệt biên bản %s. Số hạng mục: %d/%d. Tổng chi phí: %,.0f VND",
                    customerName,
                    vehicleInfo,
                    approvedItems.size(),
                    allItems.size(),
                    totalCost
            );

            String link = "/technician/maintenance-record/" + record.getRecordId();

            notificationService.createNotification(
                    schedule.getTechnician().getAccount().getAccountId(),
                    message,
                    link
            );
        }
    }

    /**
     * Xử lý khi VNPay callback thanh toán thành công
     * - Tạo payment record
     * - Cập nhật invoice status = PAID
     * - Cập nhật schedule status = COMPLETED
     */
    @Override
    @Transactional
    public void processVNPaySuccess(Integer scheduleId, String txnRef, Long amount, String method) {
        // Tìm schedule
        MaintenanceSchedule schedule = maintenanceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch bảo dưỡng với ID: " + scheduleId));

        // Tìm maintenance record
        List<MaintenanceRecord> records = maintenanceRecordRepository.findByMaintenanceSchedule(schedule);
        if (records.isEmpty()) {
            throw new RuntimeException("Lịch bảo dưỡng chưa có biên bản sửa chữa");
        }
        MaintenanceRecord record = records.get(0);

        // Tìm invoice
        Invoice invoice = invoiceRepository.findByMaintenanceRecord(record)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // Kiểm tra xem đã có payment chưa (tránh duplicate)
        List<Payment> existingPayments = paymentRepository.findByInvoice(invoice);
        boolean alreadyPaid = existingPayments.stream()
                .anyMatch(p -> txnRef.equals(p.getTransactionReference()));
        
        if (alreadyPaid) {
            System.out.println("Payment already processed for txnRef: " + txnRef);
            return;
        }

        // Tính tổng tiền từ invoice (total_labor_cost + total_part_cost)
        BigDecimal invoiceTotalAmount = invoice.getTotalLaborCost().add(invoice.getTotalPartCost());
        
        System.out.println("Invoice total amount: " + invoiceTotalAmount + " VND");
        System.out.println("VNPay callback amount: " + amount + " VND");

        // Tạo payment record mới với TỔNG TIỀN TỪ INVOICE (không phải từ VNPay callback)
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setMethod(method != null ? method : "BANK"); // VNPay = BANK/BANKING
        payment.setAmount(invoiceTotalAmount); // ⚠️ LẤY TỪ INVOICE, KHÔNG PHẢI TỪ VNPAY
        payment.setTransactionReference(txnRef);
        paymentRepository.save(payment);
        
        System.out.println("✅ Payment saved with amount: " + invoiceTotalAmount + " VND");

        // Cập nhật invoice status
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        // Cập nhật schedule status thành COMPLETED
        schedule.setStatus("COMPLETED");
        maintenanceScheduleRepository.save(schedule);

        System.out.println("Payment processed successfully for scheduleId: " + scheduleId + ", txnRef: " + txnRef);
    }
    
    @Override
    public RevenueResponse calculateRevenue(LocalDate date, String type) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        String period;
        
        switch (type.toLowerCase()) {
            case "day":
                // Tính doanh thu trong 1 ngày
                startDate = date.atStartOfDay();
                endDate = date.plusDays(1).atStartOfDay();
                period = String.format("%02d/%02d/%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
                break;
                
            case "week":
                // Tính doanh thu trong 1 tuần (từ thứ 2 đến chủ nhật)
                LocalDate monday = date.with(java.time.DayOfWeek.MONDAY);
                startDate = monday.atStartOfDay();
                endDate = monday.plusWeeks(1).atStartOfDay();
                int weekNumber = date.get(java.time.temporal.WeekFields.ISO.weekOfYear());
                period = String.format("Tuần %d - %d", weekNumber, date.getYear());
                break;
                
            case "month":
                // Tính doanh thu trong 1 tháng
                LocalDate firstDayOfMonth = date.withDayOfMonth(1);
                startDate = firstDayOfMonth.atStartOfDay();
                endDate = firstDayOfMonth.plusMonths(1).atStartOfDay();
                String monthName = date.getMonth().getDisplayName(
                    java.time.format.TextStyle.FULL, 
                    new java.util.Locale("vi", "VN")
                );
                period = String.format("Tháng %s năm %d", monthName, date.getYear());
                break;
                
            case "year":
                // Tính doanh thu trong 1 năm
                LocalDate firstDayOfYear = LocalDate.of(date.getYear(), 1, 1);
                startDate = firstDayOfYear.atStartOfDay();
                endDate = firstDayOfYear.plusYears(1).atStartOfDay();
                period = String.format("Năm %d", date.getYear());
                break;
                
            default:
                throw new IllegalArgumentException("Invalid type: " + type + ". Must be one of: day, week, month, year");
        }
        
        // Tính tổng doanh thu từ Invoice với status = PAID
        BigDecimal totalRevenue = invoiceRepository.calculateRevenueBetween(startDate, endDate);
        
        return new RevenueResponse(
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            period,
            type
        );
    }
}
