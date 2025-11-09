package swp.group4.be_ev_service_center_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp.group4.be_ev_service_center_management.entity.Invoice;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Optional<Invoice> findByMaintenanceRecord(MaintenanceRecord maintenanceRecord);

    // Find invoices between two datetimes
    List<Invoice> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // Aggregate queries (native) to group revenue/cost by day/month/year
    @Query(value =
        "SELECT DATE(i.created_at) as period, COUNT(*) as invoices, SUM(i.total_amount) as revenue, SUM(i.total_part_cost) as cost " +
        "FROM invoice i WHERE i.created_at BETWEEN :from AND :to GROUP BY DATE(i.created_at) ORDER BY DATE(i.created_at)",
        nativeQuery = true)
    List<Object[]> aggregateByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value =
        "SELECT CONCAT(YEAR(i.created_at), '-', LPAD(MONTH(i.created_at),2,'0')) as period, COUNT(*) as invoices, SUM(i.total_amount) as revenue, SUM(i.total_part_cost) as cost " +
        "FROM invoice i WHERE i.created_at BETWEEN :from AND :to GROUP BY YEAR(i.created_at), MONTH(i.created_at) ORDER BY YEAR(i.created_at), MONTH(i.created_at)",
        nativeQuery = true)
    List<Object[]> aggregateByMonth(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value =
        "SELECT YEAR(i.created_at) as period, COUNT(*) as invoices, SUM(i.total_amount) as revenue, SUM(i.total_part_cost) as cost " +
        "FROM invoice i WHERE i.created_at BETWEEN :from AND :to GROUP BY YEAR(i.created_at) ORDER BY YEAR(i.created_at)",
        nativeQuery = true)
    List<Object[]> aggregateByYear(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}