package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.*;

import java.util.List;

public interface MaintenanceScheduleManagementService {

    List<MaintenanceScheduleDTO> getSchedulesByCustomerId(Integer customerId);

    List<MaintenanceScheduleResponse> getAllSchedules();

    MaintenanceScheduleResponse getScheduleById(Integer scheduleId);

    MaintenanceScheduleResponse updateScheduleStatus(Integer scheduleId, UpdateMaintenanceScheduleRequest request);

    MaintenanceScheduleResponse assignTechnician(Integer scheduleId, AssignTechnicianRequest request);

    List<MaintenanceScheduleResponse> searchByCustomerName(String name);

    List<MaintenanceScheduleResponse> searchByLicensePlate(String plate);

    List<MaintenanceScheduleResponse> searchByStatus(String status);

    MaintenanceScheduleResponse bookSchedule(BookScheduleRequest request, Integer customerId);

    List<TimeSlotResponse> getAvailableSlots(Integer centerId, String date);

    List<AppointmentResponse> getAppointments(String keyword);

    List<PaymentManagementResponse> getPaymentList();
}