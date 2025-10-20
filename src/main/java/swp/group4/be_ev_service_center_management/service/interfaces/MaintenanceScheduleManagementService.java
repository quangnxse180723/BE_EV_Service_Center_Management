package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.AssignTechnicianRequest;
import swp.group4.be_ev_service_center_management.dto.request.BookScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.request.UpdateMaintenanceScheduleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceScheduleResponse;

import java.util.List;

public interface MaintenanceScheduleManagementService {

    /**
     * Lấy tất cả lịch hẹn
     */
    List<MaintenanceScheduleResponse> getAllSchedules();

    /**
     * Lấy chi tiết lịch hẹn
     */
    MaintenanceScheduleResponse getScheduleById(Integer scheduleId);

    /**
     * Cập nhật trạng thái lịch hẹn (Nhân viên xử lý)
     */
    MaintenanceScheduleResponse updateScheduleStatus(Integer scheduleId, UpdateMaintenanceScheduleRequest request);

    /**
     * Gán kỹ thuật viên cho lịch hẹn
     */
    MaintenanceScheduleResponse assignTechnician(Integer scheduleId, AssignTechnicianRequest request);

    /**
     * Tìm kiếm lịch hẹn theo tên khách hàng
     */
    List<MaintenanceScheduleResponse> searchByCustomerName(String name);

    /**
     * Tìm kiếm lịch hẹn theo biển số xe
     */
    List<MaintenanceScheduleResponse> searchByLicensePlate(String plate);

    /**
     * Tìm kiếm lịch hẹn theo trạng thái
     */
    List<MaintenanceScheduleResponse> searchByStatus(String status);

    /**
     * Đặt lịch bảo dưỡng (Customer)
     */
    MaintenanceScheduleResponse bookSchedule(BookScheduleRequest request, Integer customerId);
}