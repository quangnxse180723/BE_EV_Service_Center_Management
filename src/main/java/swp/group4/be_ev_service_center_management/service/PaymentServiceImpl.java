package swp.group4.be_ev_service_center_management.service;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.PaymentResponse;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.entity.Payment;
import swp.group4.be_ev_service_center_management.exception.ResourceNotFoundException;
import swp.group4.be_ev_service_center_management.repository.InvoiceRepository;
import swp.group4.be_ev_service_center_management.repository.PaymentRepository;
import swp.group4.be_ev_service_center_management.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;

    @Override
    public PaymentResponse pay(PaymentRequest req) {
        Invoice invoice = invoiceRepo.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(BigDecimal.valueOf(req.getAmount()));
        payment.setMethod(req.getMethod());

        Payment saved = paymentRepo.save(payment);

        invoice.setStatus("PAID");
        invoiceRepo.save(invoice);

        PaymentResponse res = new PaymentResponse();
        res.setPaymentId(saved.getPaymentId());
        res.setInvoiceId(invoice.getInvoiceId());
        res.setAmount(req.getAmount());
        res.setMethod(saved.getMethod());

        return res;
    }
}