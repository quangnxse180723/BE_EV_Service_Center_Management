package swp.group4.be_ev_service_center_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ScheduleService scheduleService;

    @PostMapping("/schedules/book")
    public ResponseEntity<ScheduleResponse> bookSchedule(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(scheduleService.bookSchedule(request));
    }

    @GetMapping("/schedules/{customerId}")
    public ResponseEntity<List<ScheduleResponse>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(scheduleService.getByCustomer(customerId));
    }
}