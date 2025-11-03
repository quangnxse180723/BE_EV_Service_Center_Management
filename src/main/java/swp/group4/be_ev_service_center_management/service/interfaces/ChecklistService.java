package swp.group4.be_ev_service_center_management.service.interfaces;

import swp.group4.be_ev_service_center_management.dto.request.UpdateChecklistRequest;
import swp.group4.be_ev_service_center_management.dto.response.ChecklistResponse;

public interface ChecklistService {
    /**
     * Lấy checklist dựa trên scheduleId
     * @param scheduleId ID của schedule
     * @return ChecklistResponse
     */
    ChecklistResponse getChecklistByScheduleId(Integer scheduleId);

    /**
     * Cập nhật checklist items
     * @param scheduleId ID của schedule
     * @param request Dữ liệu cập nhật
     * @return ChecklistResponse đã cập nhật
     */
    ChecklistResponse updateChecklist(Integer scheduleId, UpdateChecklistRequest request);

    /**
     * Gửi checklist cho khách hàng duyệt
     * @param scheduleId ID của schedule
     */
    void submitForApproval(Integer scheduleId);
}
