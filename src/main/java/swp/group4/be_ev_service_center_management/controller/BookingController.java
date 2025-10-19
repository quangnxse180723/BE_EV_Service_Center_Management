package swp.group4.be_ev_service_center_management.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.BookingResponse;
import swp.group4.be_ev_service_center_management.service.BookingService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request, BindingResult br) {
        if (br.hasErrors()) {
            List<String> errors = br.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        BookingResponse created = bookingService.createBooking(request);
        if (created != null && created.getId() != null) {
            URI location = URI.create("/api/bookings/" + created.getId());
            return ResponseEntity.created(location).body(created);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer id,
                                           @Valid @RequestBody BookingRequest request,
                                           BindingResult br) {
        if (br.hasErrors()) {
            List<String> errors = br.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        BookingResponse updated = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Integer id) {
        BookingResponse resp = bookingService.getBookingById(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * List / search bookings.
     * - If customerId present -> returns all bookings of that customer (non-paged).
     * - Otherwise uses pageable search via service.searchBookings.
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false) String q,
                                  @RequestParam(value = "customerId", required = false) Integer customerId,
                                  @PageableDefault(size = 20) Pageable pageable) {
        if (customerId != null) {
            List<BookingResponse> list = bookingService.getBookingsByCustomer(customerId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(list.size()));
            return new ResponseEntity<>(list, headers, HttpStatus.OK);
        }

        Page<BookingResponse> page = bookingService.searchBookings(q, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Update booking status (e.g. PENDING -> CONFIRMED -> CANCELLED).
     */


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}