package swp.group4.be_ev_service_center_management.service.interfaces;

import java.util.List;

import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketListResponse;

public interface ServiceTicketService {
    /**
     * Lấy chi tiết phiếu dịch vụ cho kỹ thuật viên
     * @param scheduleId ID của schedule
     * @return ServiceTicketDetailResponse
     */
    ServiceTicketDetailResponse getServiceTicketDetail(Integer scheduleId);
    List<ServiceTicketListResponse> getServiceTickets(Integer technicianId);
}
