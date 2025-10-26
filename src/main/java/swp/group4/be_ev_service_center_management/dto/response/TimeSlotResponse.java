package swp.group4.be_ev_service_center_management.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TimeSlotResponse {
    private Integer slotId;
    private Integer centerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}