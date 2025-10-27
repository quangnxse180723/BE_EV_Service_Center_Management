package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailResponse;

public interface InvoiceService {
    InvoiceDetailResponse getInvoiceDetailByScheduleId(Integer scheduleId);
}
