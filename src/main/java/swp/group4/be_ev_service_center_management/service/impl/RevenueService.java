package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.RevenueGroupDTO;
import swp.group4.be_ev_service_center_management.dto.response.RevenueSummary;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.repository.InvoiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final InvoiceRepository invoiceRepository;

    public RevenueSummary getSummary(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        List<Invoice> invoices = invoiceRepository.findByCreatedAtBetween(from, to);
        double totalRevenue = invoices.stream().mapToDouble(i -> safeDouble(i.getTotalAmount())).sum();
        double totalCost = invoices.stream().mapToDouble(i -> safeDouble(i.getTotalPartCost())).sum();
        long count = invoices.size();
        return new RevenueSummary(totalRevenue, totalCost, totalRevenue - totalCost, count);
    }

    public List<RevenueGroupDTO> getGrouped(LocalDate fromDate, LocalDate toDate, String groupBy) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        List<Object[]> rows;
        if ("month".equalsIgnoreCase(groupBy)) {
            rows = invoiceRepository.aggregateByMonth(from, to);
        } else if ("year".equalsIgnoreCase(groupBy)) {
            rows = invoiceRepository.aggregateByYear(from, to);
        } else {
            rows = invoiceRepository.aggregateByDay(from, to);
        }

        return rows.stream().map(r -> {
            String period = r[0] == null ? "" : r[0].toString();
            long invoices = r[1] == null ? 0L : ((Number) r[1]).longValue();
            double revenue = r[2] == null ? 0d : ((Number) r[2]).doubleValue();
            double cost = r[3] == null ? 0d : ((Number) r[3]).doubleValue();
            return new RevenueGroupDTO(period, invoices, revenue, cost, revenue - cost);
        }).collect(Collectors.toList());
    }

    private double safeDouble(java.math.BigDecimal d) {
        return d == null ? 0d : d.doubleValue();
    }
}
