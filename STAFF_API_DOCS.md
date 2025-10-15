# 📋 Staff APIs Documentation

## Base URL
```
http://localhost:8080/api/staff
```

---

## 🏠 Dashboard

### GET `/api/staff/{staffId}/dashboard`
Lấy thống kê dashboard cho Staff

**Response:**
```json
{
  "totalSchedulesToday": 9,        // Số lịch hẹn hôm nay (xanh lá)
  "vehiclesNeedRepair": 3,         // Xe cần sửa = PENDING + CONFIRMED (đỏ)
  "completedToday": 2,             // Đã hoàn thành hôm nay (tím)
  "totalPaymentsToday": 2,         // Tổng thanh toán hôm nay (vàng)
  "inProgressSchedules": 4,        // Đang sửa
  "availableTechnicians": 3,       // Thợ rảnh
  "busyTechnicians": 2,            // Thợ bận
  "totalRevenueToday": 5000000     // Tổng doanh thu hôm nay (VND)
}
```

---

## 📅 Quản lý lịch hẹn

### GET `/api/staff/{staffId}/schedules/today`
Xem tất cả lịch hẹn hôm nay

**Response:**
```json
[
  {
    "scheduleId": 1,
    "vehicleId": 10,
    "vehiclePlateNumber": "29A-12345",
    "vehicleModel": "VinFast VF8",
    "customerId": 5,
    "customerName": "Nguyễn Văn A",
    "customerPhone": "0901234567",
    "scheduledDate": "2025-10-14T09:00:00",
    "timeSlot": "09:00 - 10:00",
    "packageName": "Bảo dưỡng 10,000 km",
    "status": "PENDING",
    "technicianName": null,
    "technicianId": null,
    "createdAt": "2025-10-13T15:30:00"
  }
]
```

### GET `/api/staff/{staffId}/schedules?status={status}`
Lọc lịch hẹn theo trạng thái

**Params:**
- `status`: PENDING | CONFIRMED | IN_PROGRESS | DONE | CANCELLED

**Response:** Giống như `/schedules/today`

### GET `/api/staff/schedules/{scheduleId}`
Xem chi tiết 1 lịch hẹn

**Response:** Object đơn lẻ như trên

---

## 🚗 Check-in xe

### POST `/api/staff/{staffId}/check-in`
Check-in xe khách vào hệ thống để bắt đầu bảo trì

**Request Body:**
```json
{
  "scheduleId": 1,
  "notes": "Xe có tiếng kêu ở bánh trước",
  "vehicleCondition": "Good"  // Good | Fair | Poor
}
```

**Response:**
```json
{
  "scheduleId": 1,
  "vehiclePlateNumber": "29A-12345",
  "status": "IN_PROGRESS",
  "customerName": "Nguyễn Văn A",
  ...
}
```

**Business Logic:**
1. Kiểm tra schedule phải ở trạng thái CONFIRMED hoặc PENDING
2. Đổi status schedule sang IN_PROGRESS
3. Tạo MaintenanceRecord mới với:
   - checkInTime = now()
   - staff = staff hiện tại
   - status = PENDING
   - note = ghi chú từ request

---

## 👨‍🔧 Quản lý Technician

### POST `/api/staff/{staffId}/assign-technician`
Gán thợ cho lịch bảo trì

**Request Body:**
```json
{
  "scheduleId": 1,
  "technicianId": 3,
  "notes": "Giao cho thợ có kinh nghiệm với VinFast"
}
```

**Response:**
```json
{
  "scheduleId": 1,
  "technicianId": 3,
  "technicianName": "Trần Văn B",
  "status": "IN_PROGRESS",
  ...
}
```

**Business Logic:**
1. Validate technician cùng service center với staff
2. Tìm hoặc tạo MaintenanceRecord
3. Gán technician vào record
4. Update status schedule sang IN_PROGRESS

### GET `/api/staff/{staffId}/technicians/available`
Lấy danh sách thợ đang rảnh

**Response:**
```json
[
  {
    "technicianId": 3,
    "fullName": "Trần Văn B",
    "phone": "0912345678",
    "email": "tranvanb@evservice.com",
    "status": "AVAILABLE",
    "activeTasksCount": 0
  }
]
```

### GET `/api/staff/{staffId}/technicians`
Lấy tất cả thợ trong trung tâm

**Response:**
```json
[
  {
    "technicianId": 3,
    "fullName": "Trần Văn B",
    "phone": "0912345678",
    "email": "tranvanb@evservice.com",
    "status": "BUSY",
    "activeTasksCount": 2
  }
]
```

---

## 🔄 Workflow Staff

```
1. Khách đặt lịch → Schedule (PENDING)
   ↓
2. Staff xác nhận → Schedule (CONFIRMED)
   ↓
3. Khách đến → Staff check-in → Schedule (IN_PROGRESS)
   ↓                            MaintenanceRecord created
4. Staff gán thợ → Technician nhận việc
   ↓
5. Thợ làm xong → Staff duyệt → Schedule (DONE)
   ↓
6. Tạo Invoice → Khách thanh toán → Payment
```

---

## 📊 Status Codes

### MaintenanceSchedule Status:
- `PENDING` - Đợi xác nhận
- `CONFIRMED` - Đã xác nhận
- `IN_PROGRESS` - Đang sửa
- `DONE` - Hoàn thành
- `CANCELLED` - Đã hủy

### MaintenanceRecord Status:
- `PENDING` - Chờ thợ
- `IN_PROGRESS` - Đang sửa
- `WAITING_APPROVE` - Chờ khách duyệt
- `CUSTOMER_APPROVED` - Khách đã duyệt
- `COMPLETED` - Hoàn thành

### Technician Status:
- `AVAILABLE` - Rảnh (activeTasksCount = 0)
- `BUSY` - Bận (activeTasksCount > 0)

---

## 🧪 Testing với Postman/cURL

### Example: Get Dashboard
```bash
curl -X GET http://localhost:8080/api/staff/1/dashboard
```

### Example: Check-in Vehicle
```bash
curl -X POST http://localhost:8080/api/staff/1/check-in \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": 5,
    "notes": "Xe cần thay dầu",
    "vehicleCondition": "Good"
  }'
```

### Example: Assign Technician
```bash
curl -X POST http://localhost:8080/api/staff/1/assign-technician \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": 5,
    "technicianId": 3,
    "notes": "Giao cho thợ giỏi"
  }'
```

---

## ⚠️ Error Handling

### 400 Bad Request
```json
{
  "error": "Schedule must be CONFIRMED or PENDING to check-in"
}
```

### 404 Not Found
```json
{
  "error": "Staff not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Database connection failed"
}
```

---

## 🔐 Authentication (TODO)

Hiện tại chưa có authentication. Sẽ thêm JWT sau:
```
Authorization: Bearer <jwt_token>
```

---

## 📝 Notes

- Tất cả datetime đều dùng format ISO 8601: `2025-10-14T09:00:00`
- Tất cả API đều support CORS với `*` origins
- Response code thành công: 200 OK hoặc 201 Created
- Staff chỉ xem được data của service center mình thuộc về
