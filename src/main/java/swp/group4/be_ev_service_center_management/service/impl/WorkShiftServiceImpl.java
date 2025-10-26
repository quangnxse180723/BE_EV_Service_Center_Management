package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.WorkShiftResponse;
import swp.group4.be_ev_service_center_management.entity.WorkShift;
import swp.group4.be_ev_service_center_management.repository.WorkShiftRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.WorkShiftService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class WorkShiftServiceImpl implements WorkShiftService {
    private final WorkShiftRepository workShiftRepository;

    @Override
    public List<WorkShiftResponse> getWorkShiftsForTechnician(Integer technicianId) {
        List<WorkShift> shifts = workShiftRepository.findByTechnician_TechnicianId(technicianId);
        List<WorkShiftResponse> result = new ArrayList<>();
        // Map các ca đã có trong DB
        for (WorkShift shift : shifts) {
            result.add(WorkShiftResponse.builder()
                    .dayOfWeek(shift.getDayOfWeek().toString())
                    .timeRange(shift.getStartTime().toString() + "-" + shift.getEndTime().toString())
                    .canCheckIn(true) // FE sẽ quyết định cho phép check-in hay không
                    .build());
        }
        // Nếu muốn trả về đủ 7 ngày, thêm các ngày chưa có ca làm
        EnumSet<DayOfWeek> allDays = EnumSet.allOf(DayOfWeek.class);
        for (DayOfWeek day : allDays) {
            boolean exists = shifts.stream().anyMatch(s -> s.getDayOfWeek() == day);
            if (!exists) {
                result.add(WorkShiftResponse.builder()
                        .dayOfWeek(day.toString())
                        .timeRange("")
                        .canCheckIn(false)
                        .build());
            }
        }
        return result;
    }
}
