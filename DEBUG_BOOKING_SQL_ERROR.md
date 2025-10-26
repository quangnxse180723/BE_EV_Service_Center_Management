# Debug Booking SQL Error

## ✅ Fix đã hoàn thành

### 1. BookScheduleRequest.java
```java
@Data
public class BookScheduleRequest {
    private Integer customerId;
    private Integer vehicleId;
    private Integer centerId;
    private Integer slotId;  // ⭐ PHẢI CÓ field này
    private String scheduledDate;  // "2025-10-28"
    private String scheduledTime;  // "09:00"
    private Integer serviceId;
    private String notes;
}
```

### 2. MaintenanceScheduleManagementServiceImpl.java
```java
@Override
@Transactional
public MaintenanceScheduleResponse bookSchedule(BookScheduleRequest request, Integer customerId) {
    // Log để debug
    System.out.println("=== BOOKING REQUEST ===");
    System.out.println("Slot ID: " + request.getSlotId());
    System.out.println("Scheduled Date: " + request.getScheduledDate());
    System.out.println("Scheduled Time: " + request.getScheduledTime());
    
    // Tìm entities
    TimeSlot timeSlot = timeSlotRepository.findById(request.getSlotId())
            .orElseThrow(() -> new RuntimeException("Time Slot not found"));
    
    // Set timeSlot vào schedule
    schedule.setTimeSlot(timeSlot); // ⭐ QUAN TRỌNG!
    schedule.setScheduledDate(LocalDateTime.of(date, time));
    
    // ...
}
```

### 3. Test với Postman/Frontend
```json
{
  "slotId": 3,
  "scheduledDate": "2025-10-28",
  "scheduledTime": "09:00",
  "customerId": 1,
  "vehicleId": 1,
  "centerId": 1,
  "notes": "Test booking"
}
```

### Console Output Expected:
```
=== BOOKING REQUEST ===
Customer ID: 1
Vehicle ID: 1
Center ID: 1
Slot ID: 3
Scheduled Date: 2025-10-28
Scheduled Time: 09:00
====================

Hibernate: 
    insert into maintenanceschedule 
    (booking_date, center_id, created_at, customer_id, notes, 
     scheduled_date, slot_id, status, vehicle_id) 
    values (?, ?, ?, ?, ?, ?, ?, ?, ?)
```

## ✅ Build Success
```
[INFO] BUILD SUCCESS
[INFO] Total time:  7.655 s
```
