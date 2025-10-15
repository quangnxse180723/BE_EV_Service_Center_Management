package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.request.PaymentRequest;
import swp.group4.be_ev_service_center_management.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest req);
}