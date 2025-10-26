package swp.group4.be_ev_service_center_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponse {
    private Integer slotId;
    private String time;
    private Integer available;
    private Integer total;
    
    // Keep existing fields for backward compatibility
    private Integer centerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}