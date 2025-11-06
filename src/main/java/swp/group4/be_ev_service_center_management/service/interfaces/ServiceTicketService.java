package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketDetailResponse;
import swp.group4.be_ev_service_center_management.dto.response.ServiceTicketListResponse;

import java.util.List;

public interface ServiceTicketService {
    ServiceTicketDetailResponse getServiceTicketDetail(Integer scheduleId);
    List<ServiceTicketListResponse> getServiceTickets(Integer technicianId);
    
    // Update item status to DONE
    void confirmItemCompletion(Integer itemId);
    
    // Complete entire schedule
    void completeSchedule(Integer scheduleId);
}
