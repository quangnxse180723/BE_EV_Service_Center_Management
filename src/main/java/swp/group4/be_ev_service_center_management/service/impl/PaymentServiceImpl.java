package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentStatisticsResponse;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.entity.Payment;
import swp.group4.be_ev_service_center_management.repository.InvoiceRepository;
import swp.group4.be_ev_service_center_management.repository.PaymentRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.PaymentService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public List<PaymentManagementResponse> getAllPaymentsForManagement() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream().map(this::mapInvoiceToPaymentManagementResponse).collect(Collectors.toList());
    }

    @Override
    public List<PaymentManagementResponse> getCustomerPaymentHistory(int customerId) {
        List<Payment> payments = paymentRepository.findByInvoice_MaintenanceRecord_MaintenanceSchedule_Customer_CustomerId(customerId);
        return payments.stream().map(this::mapPaymentToPaymentManagementResponse).collect(Collectors.toList());
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
}
