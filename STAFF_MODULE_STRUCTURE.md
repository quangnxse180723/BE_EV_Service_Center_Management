# 🏗️ Cấu trúc Code Staff Module

## 📁 File Structure

```
src/main/java/swp/group4/be_ev_service_center_management/
│
├── 📦 entity/                                    # Database entities
│   ├── Staff.java                                ✅ Entity chính
│   ├── MaintenanceSchedule.java                  ✅ Lịch hẹn bảo trì
│   ├── MaintenanceRecord.java                    ✅ Hồ sơ bảo trì
│   ├── Technician.java                           ✅ Thông tin thợ
│   ├── Payment.java                              ✅ Thanh toán
│   ├── Vehicle.java                              ✅ Xe
│   ├── Customer.java                             ✅ Khách hàng
│   └── ServiceCenter.java                        ✅ Trung tâm dịch vụ
│
├── 📦 dto/
│   ├── request/
│   │   ├── CheckInRequest.java                   ✅ DTO check-in xe
│   │   └── AssignTechnicianRequest.java          ✅ DTO gán thợ
│   │
│   └── response/
│       ├── StaffDashboardResponse.java           ✅ Dashboard stats
│       ├── ScheduleResponse.java                 ✅ Thông tin lịch hẹn
│       └── TechnicianResponse.java               ✅ Thông tin thợ
│
├── 📦 repository/                                # Data access layer
│   ├── StaffRepository.java                      ✅ CRUD Staff
│   ├── MaintenanceScheduleRepository.java        ✅ CRUD + Query lịch hẹn
│   ├── MaintenanceRecordRepository.java          ✅ CRUD + Query hồ sơ
│   ├── TechnicianRepository.java                 ✅ CRUD + Query thợ
│   └── PaymentRepository.java                    ✅ CRUD + Query thanh toán
│
├── 📦 service/
│   ├── interfaces/
│   │   └── StaffDashboardService.java            ✅ Interface service
│   │
│   └── impl/
│       └── StaffDashboardSeviceImpl.java         ✅ Business logic
│
└── 📦 controller/
    └── StaffController.java                      ✅ REST API endpoints
```

---

## 🔗 Entity Relationships

```
Staff (Nhân viên lễ tân)
├── belongsTo: ServiceCenter (1 staff thuộc 1 trung tâm)
├── hasOne: Account (1 staff có 1 tài khoản)
└── hasMany: MaintenanceRecord (staff tạo nhiều hồ sơ bảo trì)

MaintenanceSchedule (Lịch hẹn)
├── belongsTo: Vehicle
├── belongsTo: Customer
├── belongsTo: ServiceCenter
├── belongsTo: TimeSlot
├── belongsTo: MaintenancePackage
└── hasOne: MaintenanceRecord

MaintenanceRecord (Hồ sơ bảo trì)
├── belongsTo: MaintenanceSchedule
├── belongsTo: Staff (staff check-in)
├── belongsTo: Technician (thợ thực hiện)
├── hasOne: Invoice
└── hasMany: MaintenanceItem (chi tiết công việc)

Payment (Thanh toán)
└── belongsTo: Invoice
```

---

## 🎯 Core Features Implementation

### 1️⃣ Dashboard Statistics

**File:** `StaffDashboardSeviceImpl.java` → `getDashboardStats()`

**Logic:**
```java
// Số lịch hẹn hôm nay (màu xanh lá)
countTodaySchedules(centerId, today)

// Xe cần sửa (màu đỏ)
count(PENDING) + count(CONFIRMED)

// Đã hoàn thành (màu tím)
count(DONE) where DATE = today

// Tổng thanh toán hôm nay (màu vàng)
countTodayPayments(centerId, today)
sumTodayRevenue(centerId, today)
```

**Query trong Repository:**
```java
@Query("SELECT COUNT(ms) FROM MaintenanceSchedule ms 
       WHERE ms.serviceCenter.centerId = :centerId 
       AND DATE(ms.scheduledDate) = DATE(:date)")
Integer countTodaySchedules(...)
```

---

### 2️⃣ View Schedules

**File:** `StaffDashboardSeviceImpl.java` → `getTodaySchedules()` / `getSchedulesByStatus()`

**Logic:**
```java
// Lấy schedules theo service center và thời gian/status
List<MaintenanceSchedule> schedules = 
    scheduleRepository.findByServiceCenter_CenterIdAndScheduledDateBetween(...)

// Map sang DTO response
return schedules.stream()
    .map(this::mapToScheduleResponse)
    .collect(Collectors.toList());
```

**Mapping:** 
- Vehicle → vehiclePlateNumber, vehicleModel
- Customer → customerName, customerPhone
- TimeSlot → timeSlot string
- MaintenancePackage → packageName
- MaintenanceRecord → technicianName (nếu đã gán)

---

### 3️⃣ Check-in Vehicle

**File:** `StaffDashboardSeviceImpl.java` → `checkInVehicle()`

**Logic:**
```java
1. Validate schedule status (CONFIRMED hoặc PENDING)
2. Update schedule.status = "IN_PROGRESS"
3. Create MaintenanceRecord:
   - maintenanceSchedule = schedule
   - staff = current staff
   - checkInTime = LocalDateTime.now()
   - status = "PENDING"
   - note = request.notes
4. Save và return ScheduleResponse
```

**Transaction:** `@Transactional` để đảm bảo atomic

---

### 4️⃣ Assign Technician

**File:** `StaffDashboardSeviceImpl.java` → `assignTechnician()`

**Logic:**
```java
1. Validate technician cùng service center
2. Find hoặc create MaintenanceRecord
3. Assign technician:
   record.setTechnician(technician)
4. Update schedule.status = "IN_PROGRESS"
5. Save và return ScheduleResponse
```

---

### 5️⃣ Technician Management

**File:** `StaffDashboardSeviceImpl.java` → `getAvailableTechnicians()` / `getAllTechnicians()`

**Available Logic:**
```sql
SELECT * FROM Technician 
WHERE centerId = :centerId
AND technicianId NOT IN (
  SELECT technicianId FROM MaintenanceRecord 
  WHERE status IN ('IN_PROGRESS', 'PENDING', 'WAITING_APPROVE')
)
```

**All Logic:**
```java
// Lấy tất cả technicians
List<Technician> techs = technicianRepository.findByServiceCenter_CenterId(...)

// Đếm active tasks cho mỗi thợ
techs.forEach(tech -> {
    Integer count = recordRepository.countActiveTasksByTechnician(tech.id)
    status = (count == 0) ? "AVAILABLE" : "BUSY"
})
```

---

## 🚀 API Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/staff/{staffId}/dashboard` | Dashboard stats |
| GET | `/api/staff/{staffId}/schedules/today` | Lịch hẹn hôm nay |
| GET | `/api/staff/{staffId}/schedules?status=` | Lọc theo status |
| GET | `/api/staff/schedules/{scheduleId}` | Chi tiết lịch hẹn |
| POST | `/api/staff/{staffId}/check-in` | Check-in xe |
| POST | `/api/staff/{staffId}/assign-technician` | Gán thợ |
| GET | `/api/staff/{staffId}/technicians/available` | Thợ rảnh |
| GET | `/api/staff/{staffId}/technicians` | Tất cả thợ |

---

## 📝 DTO Classes

### CheckInRequest
```java
{
    Integer scheduleId;
    String notes;
    String vehicleCondition; // Good, Fair, Poor
}
```

### AssignTechnicianRequest
```java
{
    Integer scheduleId;
    Integer technicianId;
    String notes;
}
```

### StaffDashboardResponse (khớp UI)
```java
{
    Integer totalSchedulesToday;      // Xanh lá
    Integer vehiclesNeedRepair;       // Đỏ
    Integer completedToday;           // Tím
    Integer totalPaymentsToday;       // Vàng
    Integer inProgressSchedules;
    Integer availableTechnicians;
    Integer busyTechnicians;
    BigDecimal totalRevenueToday;
}
```

### ScheduleResponse
```java
{
    Integer scheduleId;
    Integer vehicleId;
    String vehiclePlateNumber;
    String vehicleModel;
    Integer customerId;
    String customerName;
    String customerPhone;
    LocalDateTime scheduledDate;
    String timeSlot;
    String packageName;
    String status;
    String technicianName;
    Integer technicianId;
    LocalDateTime createdAt;
}
```

### TechnicianResponse
```java
{
    Integer technicianId;
    String fullName;
    String phone;
    String email;
    String status;            // AVAILABLE | BUSY
    Integer activeTasksCount;
}
```

---

## 🔄 Status Flow

### MaintenanceSchedule Status:
```
PENDING → CONFIRMED → IN_PROGRESS → DONE
                  ↓
              CANCELLED
```

### MaintenanceRecord Status:
```
PENDING → IN_PROGRESS → WAITING_APPROVE → CUSTOMER_APPROVED → COMPLETED
```

---

## 🛠️ Dependencies

### Repositories Used:
- ✅ `StaffRepository` - CRUD staff
- ✅ `MaintenanceScheduleRepository` - Query lịch hẹn
- ✅ `MaintenanceRecordRepository` - Query hồ sơ bảo trì
- ✅ `TechnicianRepository` - Query thợ available/busy
- ✅ `PaymentRepository` - Query thanh toán

### Key Annotations:
- `@Service` - Service layer
- `@RestController` - REST API controller
- `@Repository` - Data access
- `@Transactional` - Database transactions
- `@Query` - Custom JPQL queries
- `@RequiredArgsConstructor` - Lombok constructor injection

---

## ✅ Features Completed

- [x] Staff entity với relationships
- [x] 5 DTOs (2 request, 3 response)
- [x] 5 Repositories với custom queries
- [x] StaffDashboardService với 8 methods
- [x] StaffController với 8 endpoints
- [x] Dashboard statistics (khớp UI)
- [x] Check-in workflow
- [x] Assign technician workflow
- [x] Technician availability tracking
- [x] Payment tracking
- [x] Full documentation

---

## 🎨 UI Mapping

| UI Element | API Field | Color |
|------------|-----------|-------|
| Số lịch hẹn hôm nay | `totalSchedulesToday` | 🟢 Xanh lá |
| Xe cần sửa | `vehiclesNeedRepair` | 🔴 Đỏ |
| Đã hoàn thành | `completedToday` | 🟣 Tím |
| Tổng thanh toán hôm nay | `totalPaymentsToday` | 🟡 Vàng |

---

## 🧪 Next Steps

1. ✅ Test API với Postman
2. ⏳ Add JWT authentication
3. ⏳ Add error handling với custom exceptions
4. ⏳ Add validation với `@Valid`
5. ⏳ Add pagination cho list endpoints
6. ⏳ Add search/filter functionality
7. ⏳ Add unit tests
8. ⏳ Integrate với Frontend

---

## 📚 Related Docs

- [STAFF_API_DOCS.md](./STAFF_API_DOCS.md) - API documentation
- [MYSQL_SETUP.md](./MYSQL_SETUP.md) - Database setup
- [README.md](./README.md) - Project overview
