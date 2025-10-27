package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.InvoiceDetailResponse;
import swp.group4.be_ev_service_center_management.entity.*;
import swp.group4.be_ev_service_center_management.repository.*;
import swp.group4.be_ev_service_center_management.service.interfaces.InvoiceService;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceRecordRepository recordRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public InvoiceDetailResponse getInvoiceDetailByScheduleId(Integer scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        MaintenanceRecord record = recordRepository.findByMaintenanceSchedule(schedule)
                .stream().findFirst().orElse(null);
        if (record == null) throw new RuntimeException("No maintenance record found");
        Optional<Invoice> invoiceOpt = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getMaintenanceRecord().getRecordId().equals(record.getRecordId()))
                .findFirst();
        if (invoiceOpt.isEmpty()) throw new RuntimeException("No invoice found");
        Invoice invoice = invoiceOpt.get();
        Optional<Payment> paymentOpt = paymentRepository.findAll().stream()
                .filter(p -> p.getInvoice().getInvoiceId().equals(invoice.getInvoiceId()))
                .findFirst();
        String paymentMethod = paymentOpt.map(Payment::getMethod).orElse("N/A");
        return InvoiceDetailResponse.builder()
                .customerName(schedule.getCustomer().getFullName())
                .vehicleName(schedule.getVehicle().getModel())
                .licensePlate(schedule.getVehicle().getLicensePlate())
                .maintenanceType(record.getStatus())
                .repairNote(record.getNote())
                .detailButtonLabel("Chi tiết")
                .totalCost(invoice.getTotalAmount() != null ? invoice.getTotalAmount().toString() : "0")
                .paymentMethod(paymentMethod)
                .status(invoice.getStatus())
                .build();
    }
}
