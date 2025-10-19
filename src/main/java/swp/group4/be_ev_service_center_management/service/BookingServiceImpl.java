
package swp.group4.be_ev_service_center_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.BookingResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    @Autowired
    public BookingServiceImpl(MaintenanceScheduleRepository scheduleRepository,
                              CustomerRepository customerRepository,
                              VehicleRepository vehicleRepository,
                              ServiceCenterRepository serviceCenterRepository) {
        this.scheduleRepository = scheduleRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceCenterRepository = serviceCenterRepository;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is required");
        }
        if (request.getCustomerId() == null || !customerRepository.existsById(request.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found");
        }
        if (request.getVehicleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is required");
        }
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle not found"));
        if (!Objects.equals(vehicle.getCustomer() != null ? vehicle.getCustomer().getCustomerId() : null, request.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle does not belong to the customer");
        }
        if (request.getServiceCenterId() == null || !serviceCenterRepository.existsById(request.getServiceCenterId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service center not found");
        }
        if (request.getPreferredStart() == null || request.getPreferredStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "preferredStart must be in the future");
        }

        MaintenanceSchedule ms = new MaintenanceSchedule();
        Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        ServiceCenter center = serviceCenterRepository.findById(request.getServiceCenterId()).orElse(null);

        ms.setCustomer(customer);
        ms.setVehicle(vehicle);
        ms.setServiceCenter(center);
        ms.setBookingDate(LocalDateTime.now());
        ms.setScheduledDate(request.getPreferredStart());
        ms.setStatus("PENDING");

        MaintenanceSchedule saved = scheduleRepository.save(ms);
        return toResponse(saved);
    }

    @Override
    public BookingResponse updateBooking(Integer id, BookingRequest request) {
        MaintenanceSchedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (request.getCustomerId() != null && (existing.getCustomer() == null ||
                !Objects.equals(existing.getCustomer().getCustomerId(), request.getCustomerId()))) {
            Customer c = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found"));
            existing.setCustomer(c);
        }

        if (request.getVehicleId() != null && (existing.getVehicle() == null ||
                !Objects.equals(existing.getVehicle().getVehicleId(), request.getVehicleId()))) {
            Vehicle v = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle not found"));
            if (existing.getCustomer() != null && (v.getCustomer() == null ||
                    !Objects.equals(v.getCustomer().getCustomerId(), existing.getCustomer().getCustomerId()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle does not belong to the customer");
            }
            existing.setVehicle(v);
        }

        if (request.getServiceCenterId() != null) {
            ServiceCenter sc = serviceCenterRepository.findById(request.getServiceCenterId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service center not found"));
            existing.setServiceCenter(sc);
        }

        if (request.getPreferredStart() != null) existing.setScheduledDate(request.getPreferredStart());

        MaintenanceSchedule updated = scheduleRepository.save(existing);
        return toResponse(updated);
    }

    @Override
    public BookingResponse confirmBooking(Integer id) {
        MaintenanceSchedule s = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if ("CONFIRMED".equalsIgnoreCase(s.getStatus())) {
            return toResponse(s);
        }
        s.setStatus("CONFIRMED");
        MaintenanceSchedule updated = scheduleRepository.save(s);
        return toResponse(updated);
    }

    @Override
    public BookingResponse cancelBooking(Integer id) {
        MaintenanceSchedule s = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if ("CANCELLED".equalsIgnoreCase(s.getStatus())) {
            return toResponse(s);
        }
        s.setStatus("CANCELLED");
        MaintenanceSchedule updated = scheduleRepository.save(s);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Integer id) {
        MaintenanceSchedule s = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return toResponse(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(Integer customerId) {
        return scheduleRepository.findAll().stream()
                .filter(ms -> ms.getCustomer() != null && Objects.equals(ms.getCustomer().getCustomerId(), customerId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> searchBookings(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return scheduleRepository.findAll(pageable).map(this::toResponse);
        }
        String term = q.trim().toLowerCase();
        List<BookingResponse> filtered = scheduleRepository.findAll().stream()
                .filter(ms -> {
                    if (ms == null) return false;
                    if (ms.getStatus() != null && ms.getStatus().toLowerCase().contains(term)) return true;
                    if (ms.getCustomer() != null && ms.getCustomer().getFullName() != null &&
                            ms.getCustomer().getFullName().toLowerCase().contains(term)) return true;
                    return false;
                })
                .map(this::toResponse)
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (int) pageable.getOffset();
        if (start >= filtered.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<BookingResponse> content = filtered.subList(start, end);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteBooking(Integer id) {
        MaintenanceSchedule s = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        scheduleRepository.delete(s);
    }

    // --- helpers ---

    private BookingResponse toResponse(MaintenanceSchedule s) {
        if (s == null) return null;
        BookingResponse r = new BookingResponse();
        r.setId(s.getScheduleId());
        if (s.getCustomer() != null) {
            r.setCustomerId(s.getCustomer().getCustomerId());
        }
        if (s.getVehicle() != null) {
            BookingResponse.VehicleResponse vr = new BookingResponse.VehicleResponse();
            vr.setId(s.getVehicle().getVehicleId());
            r.setVehicle(vr);
        }
        if (s.getServiceCenter() != null) {
            r.setServiceCenterId(s.getServiceCenter().getCenterId());
        }
        r.setPreferredStart(convertToOffset(s.getScheduledDate()));
        r.setCreatedAt(convertToOffset(s.getCreatedAt()));
        r.setStatus(s.getStatus());
        return r;
    }

    private OffsetDateTime convertToOffset(LocalDateTime dt) {
        if (dt == null) return null;
        ZoneId zid = ZoneId.systemDefault();
        ZoneOffset offset = zid.getRules().getOffset(dt);
        return dt.atOffset(offset);
    }
}