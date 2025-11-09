package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.TimeSlotResponse;
import swp.group4.be_ev_service_center_management.entity.TimeSlot;
import swp.group4.be_ev_service_center_management.repository.TimeSlotRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.TimeSlotService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public List<TimeSlotResponse> getAvailableTimeSlots(Integer centerId, LocalDate date) {
        List<TimeSlot> slots = timeSlotRepository.findByServiceCenter_CenterIdAndDateAndStatus(centerId, date, "AVAILABLE");
        return slots.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private TimeSlotResponse toResponse(TimeSlot slot) {
        TimeSlotResponse response = new TimeSlotResponse();
        response.setSlotId(slot.getSlotId());
        response.setCenterId(slot.getServiceCenter().getCenterId());
        response.setDate(slot.getDate());
        response.setStartTime(slot.getStartTime());
        response.setEndTime(slot.getEndTime());
        response.setStatus(slot.getStatus());
        return response;
    }
}