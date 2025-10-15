package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import java.util.List;

public interface ScheduleService {
    ScheduleResponse bookSchedule(BookingRequest request);
    List<ScheduleResponse> getByCustomer(Integer customerId);
    List<ScheduleResponse> getByCenter(Integer centerId);
}