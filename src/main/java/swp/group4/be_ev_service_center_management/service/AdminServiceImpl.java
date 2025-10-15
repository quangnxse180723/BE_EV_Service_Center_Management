package swp.group4.be_ev_service_center_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.service.AdminService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final MaintenanceScheduleRepository scheduleRepo;

    @Override
    public List<ScheduleResponse> getSchedulesByCenter(Integer centerId) {
        return scheduleRepo.findByServiceCenterCenterId(centerId)
                .stream().map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    private ScheduleResponse entityToResponse(MaintenanceSchedule s) {
        ScheduleResponse res = new ScheduleResponse();
        res.setScheduleId(s.getScheduleId());
        res.setVehicleId(s.getVehicle().getVehicleId());
        res.setCenterId(s.getServiceCenter().getCenterId());
        res.setCustomerId(s.getCustomer().getCustomerId());
        //res.setSlotId(s.getSlot().getSlotId());
        res.setBookingDate(s.getBookingDate());
        res.setScheduledDate(s.getScheduledDate());
        res.setStatus(s.getStatus());
        return res;
    }
}