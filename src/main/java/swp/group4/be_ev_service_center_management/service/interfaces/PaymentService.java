package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentStatisticsResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentManagementResponse> getAllPaymentsForManagement();
    List<PaymentManagementResponse> getCustomerPaymentHistory(int customerId);
    PaymentManagementResponse getPaymentById(int paymentId);

    // New methods
    PaymentManagementResponse createPayment(PaymentRequest paymentRequest);
    PaymentManagementResponse updatePaymentStatus(int paymentId, String status);
    PaymentStatisticsResponse getPaymentStatistics(int customerId, int year);
    PaymentManagementResponse payInvoice(int invoiceId, PaymentRequest paymentRequest);
}