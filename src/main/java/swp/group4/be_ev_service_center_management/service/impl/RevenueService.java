package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.RevenueGroupDTO;
import swp.group4.be_ev_service_center_management.dto.response.RevenueSummary;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.repository.InvoiceRepository;
import swp.group4.be_ev_service_center_management.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public RevenueSummary getSummary(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        
        // Tổng doanh thu = SUM(Payment.amount) WHERE status = 'PAID'
        Double totalRevenue = paymentRepository.getTotalPaidAmountBetween(from, to);
        if (totalRevenue == null) totalRevenue = 0.0;
        
        // Tổng chi phí = SUM(Invoice.totalPartCost) WHERE status = 'PAID'
        Double totalCost = invoiceRepository.getTotalPartCostPaidBetween(from, to);
        if (totalCost == null) totalCost = 0.0;
        
        // Đếm số invoice PAID
        List<Invoice> invoices = invoiceRepository.findByStatusAndCreatedAtBetween("PAID", from, to);
        long count = invoices.size();
        
        return new RevenueSummary(totalRevenue, totalCost, totalRevenue - totalCost, count);
    }

    public List<RevenueGroupDTO> getGrouped(LocalDate fromDate, LocalDate toDate, String groupBy) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        
        List<Object[]> paymentRows;
        List<Object[]> costRows;
        
        // Lấy dữ liệu revenue từ Payment (status = PAID)
        if ("month".equalsIgnoreCase(groupBy)) {
            paymentRows = paymentRepository.aggregatePaymentByMonth(from, to);
            costRows = invoiceRepository.aggregateCostByMonth(from, to);
        } else if ("year".equalsIgnoreCase(groupBy)) {
            paymentRows = paymentRepository.aggregatePaymentByYear(from, to);
            costRows = invoiceRepository.aggregateCostByYear(from, to);
        } else {
            paymentRows = paymentRepository.aggregatePaymentByDay(from, to);
            costRows = invoiceRepository.aggregateCostByDay(from, to);
        }
        
        // Map cost theo period
        Map<String, Double> costMap = new HashMap<>();
        for (Object[] row : costRows) {
            String period = row[0] == null ? "" : row[0].toString();
            double cost = row[1] == null ? 0d : ((Number) row[1]).doubleValue();
            costMap.put(period, cost);
        }
        
        // Kết hợp payment và cost
        List<RevenueGroupDTO> result = new ArrayList<>();
        for (Object[] row : paymentRows) {
            String period = row[0] == null ? "" : row[0].toString();
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            double revenue = row[2] == null ? 0d : ((Number) row[2]).doubleValue();
            double cost = costMap.getOrDefault(period, 0.0);
            
            result.add(new RevenueGroupDTO(period, count, revenue, cost, revenue - cost));
        }
        
        return result;
    }
}
