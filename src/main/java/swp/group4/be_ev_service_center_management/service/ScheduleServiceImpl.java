package swp.group4.be_ev_service_center_management.service;

import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.BookingRequest;
import swp.group4.be_ev_service_center_management.dto.response.ScheduleResponse;
import swp.group4.be_ev_service_center_management.entity.MaintenanceSchedule;
import swp.group4.be_ev_service_center_management.exception.ResourceNotFoundException;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.service.ScheduleService;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final MaintenanceScheduleRepository scheduleRepo;
    private final VehicleRepository vehicleRepo;
    private final ServiceCenterRepository centerRepo;
    private final CustomerRepository customerRepo;

    public ScheduleServiceImpl(
            MaintenanceScheduleRepository scheduleRepo,
            VehicleRepository vehicleRepo,
            ServiceCenterRepository centerRepo,
            CustomerRepository customerRepo
    ) {
        this.scheduleRepo = scheduleRepo;
        this.vehicleRepo = vehicleRepo;
        this.centerRepo = centerRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public ScheduleResponse bookSchedule(BookingRequest req) {
        MaintenanceSchedule s = new MaintenanceSchedule();
        s.setVehicle(vehicleRepo.findById(req.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")));
        s.setServiceCenter(centerRepo.findById(req.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("Center not found")));
        s.setCustomer(customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found")));

        //s.setScheduledDate(req.getScheduledDate().toInstant()
         //       .atZone(ZoneId.systemDefault())
           //     .toLocalDateTime());
        s.setBookingDate(LocalDateTime.now());
        MaintenanceSchedule saved = scheduleRepo.save(s);
        return entityToResponse(saved);
    }

    @Override
    public List<ScheduleResponse> getByCustomer(Integer customerId) {
        return scheduleRepo.findByCustomerCustomerId(customerId)
                .stream().map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getByCenter(Integer centerId) {
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
        // Nếu không có slot hoặc package, bỏ qua các dòng sau
        // res.setSlotId(null);
        // res.setPackageId(null);
        res.setBookingDate(s.getBookingDate());
        res.setScheduledDate(s.getScheduledDate());
        res.setStatus(s.getStatus());
        return res;
    }
}