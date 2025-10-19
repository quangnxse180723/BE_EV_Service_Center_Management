package swp.group4.be_ev_service_center_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse updateBooking(Integer id, BookingRequest request);
    BookingResponse confirmBooking(Integer id);
    BookingResponse cancelBooking(Integer id);
    BookingResponse getBookingById(Integer id);
    List<BookingResponse> getBookingsByCustomer(Integer customerId);
    Page<BookingResponse> searchBookings(String q, Pageable pageable);
    void deleteBooking(Integer id);
}