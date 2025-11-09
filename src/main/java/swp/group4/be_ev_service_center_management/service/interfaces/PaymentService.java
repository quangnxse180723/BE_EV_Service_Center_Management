package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailForStaffResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentManagementResponse;
import swp.group4.be_ev_service_center_management.dto.response.PaymentStatisticsResponse;
import swp.group4.be_ev_service_center_management.dto.response.RevenueResponse;

import java.time.LocalDate;
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
    
    // Staff - Invoice detail with maintenance checklist
    InvoiceDetailForStaffResponse getInvoiceDetailByScheduleId(Integer scheduleId);
    void sendInvoiceToCustomer(Integer scheduleId, String paymentMethod);
    
    // Customer - Approve/Modify checklist
    void customerApproveChecklist(Integer scheduleId, List<Integer> approvedItemIds);
    
    // VNPay callback - Process payment success
    void processVNPaySuccess(Integer scheduleId, String txnRef, Long amount, String method);
    
    // Revenue calculation
    RevenueResponse calculateRevenue(LocalDate date, String type);
}