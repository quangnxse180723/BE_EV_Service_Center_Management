# 📋 Staff Schedule Management API Documentation

## Base URL
```
http://localhost:8080/api/staff/{staffId}/schedule-management
```

---

## 📌 Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Tạo lịch hẹn mới |
| PUT | `/` | Cập nhật lịch hẹn |
| POST | `/{scheduleId}/confirm` | Xác nhận lịch hẹn |
| POST | `/{scheduleId}/cancel` | Hủy lịch hẹn |
| GET | `/{scheduleId}/detail` | Xem chi tiết lịch hẹn |
| POST | `/search` | Tìm kiếm/Lọc lịch hẹn |
| GET | `/date-range` | Lấy lịch theo khoảng thời gian |
| GET | `/weekly` | Lấy lịch theo tuần |
| GET | `/monthly` | Lấy lịch theo tháng |
| GET | `/customer/{customerId}/history` | Lịch sử của khách hàng |
| GET | `/vehicle/{vehicleId}/history` | Lịch sử của xe |

---

## 🔵 1. Create Schedule (Tạo lịch hẹn mới)

### Request
```http
POST /api/staff/1/schedule-management
Content-Type: application/json

{
  "customerId": 5,
  "vehicleId": 10,
  "packageId": 2,
  "scheduledDate": "2025-10-20T09:00:00",
  "slotId": 3,
  "notes": "Customer requests oil change"
}
```

### Response
```json
{
  "scheduleId": 15,
  "vehicleId": 10,
  "vehiclePlateNumber": "29A-12345",
  "vehicleModel": "Tesla Model 3",
  "customerId": 5,
  "customerName": "Nguyen Van A",
  "customerPhone": "0901234567",
  "scheduledDate": "2025-10-20T09:00:00",
  "timeSlot": "09:00 - 11:00",
  "packageName": "Basic Maintenance",
  "status": "PENDING",
  "technicianName": null,
  "technicianId": null,
  "createdAt": "2025-10-14T13:00:00"
}
```

### Status Codes
- `201 Created` - Tạo thành công
- `400 Bad Request` - Dữ liệu không hợp lệ
- `500 Internal Server Error` - Lỗi server

---

## 🔵 2. Update Schedule (Cập nhật lịch hẹn)

### Request
```http
PUT /api/staff/1/schedule-management
Content-Type: application/json

{
  "scheduleId": 15,
  "scheduledDate": "2025-10-21T10:00:00",
  "slotId": 4,
  "packageId": 3,
  "notes": "Changed to premium package"
}
```

### Notes
- Chỉ cập nhật được khi status là `PENDING` hoặc `CONFIRMED`
- Ngày hẹn phải trong tương lai

---

## 🔵 3. Confirm Schedule (Xác nhận lịch hẹn)

### Request
```http
POST /api/staff/1/schedule-management/15/confirm
```

### Response
```json
{
  "scheduleId": 15,
  "status": "CONFIRMED",
  ...
}
```

### Notes
- Chỉ confirm được khi status là `PENDING`
- Sau khi confirm, khách hàng nhận thông báo (TODO)

---

## 🔵 4. Cancel Schedule (Hủy lịch hẹn)

### Request
```http
POST /api/staff/1/schedule-management/15/cancel
Content-Type: application/json

{
  "reason": "Customer requested cancellation"
}
```

### Notes
- Không thể hủy khi status là `IN_PROGRESS` hoặc `DONE`
- Khách hàng sẽ nhận thông báo (TODO)

---

## 🔵 5. Get Schedule Detail (Xem chi tiết)

### Request
```http
GET /api/staff/1/schedule-management/15/detail
```

### Response
```json
{
  "scheduleId": 15,
  "bookingDate": "2025-10-14T13:00:00",
  "scheduledDate": "2025-10-20T09:00:00",
  "status": "CONFIRMED",
  "notes": "Customer requests oil change",
  "customerId": 5,
  "customerName": "Nguyen Van A",
  "customerPhone": "0901234567",
  "customerEmail": "nguyenvana@email.com",
  "vehicleId": 10,
  "vehiclePlate": "29A-12345",
  "vehicleModel": "Tesla Model 3",
  "vehicleVin": "5YJ3E1EA1KF123456",
  "vehicleMileage": 15000,
  "slotId": 3,
  "timeSlotStart": "09:00:00",
  "timeSlotEnd": "11:00:00",
  "packageId": 2,
  "packageName": "Basic Maintenance",
  "packageDescription": "Oil change, tire rotation, basic inspection",
  "centerId": 1,
  "centerName": "EV Service Center Hanoi",
  "technicianId": null,
  "technicianName": null,
  "maintenanceItems": []
}
```

---

## 🔵 6. Search Schedules (Tìm kiếm/Lọc)

### Request
```http
POST /api/staff/1/schedule-management/search
Content-Type: application/json

{
  "status": "PENDING",
  "customerName": "nguyen",
  "vehiclePlate": "29A",
  "dateFrom": "2025-10-01",
  "dateTo": "2025-10-31",
  "packageId": 2
}
```

### Filter Parameters
- `status` - Trạng thái: PENDING, CONFIRMED, IN_PROGRESS, DONE, CANCELLED
- `customerName` - Tên khách hàng (contains, case-insensitive)
- `vehiclePlate` - Biển số xe (contains, case-insensitive)
- `dateFrom` - Từ ngày (format: yyyy-MM-dd)
- `dateTo` - Đến ngày (format: yyyy-MM-dd)
- `packageId` - ID gói bảo trì

### Response
```json
[
  {
    "scheduleId": 15,
    "status": "PENDING",
    ...
  },
  {
    "scheduleId": 16,
    "status": "PENDING",
    ...
  }
]
```

---

## 🔵 7. Get Schedules by Date Range

### Request
```http
GET /api/staff/1/schedule-management/date-range?from=2025-10-01T00:00:00&to=2025-10-31T23:59:59
```

### Response
Danh sách schedules trong khoảng thời gian

---

## 🔵 8. Get Weekly Schedules

### Request
```http
GET /api/staff/1/schedule-management/weekly?weekStart=2025-10-14T00:00:00
```

### Response
Danh sách schedules trong tuần (7 ngày từ weekStart)

---

## 🔵 9. Get Monthly Schedules

### Request
```http
GET /api/staff/1/schedule-management/monthly?year=2025&month=10
```

### Response
Danh sách schedules trong tháng 10/2025

---

## 🔵 10. Get Customer Schedule History

### Request
```http
GET /api/staff/1/schedule-management/customer/5/history
```

### Response
Tất cả lịch hẹn của khách hàng ID 5

---

## 🔵 11. Get Vehicle Schedule History

### Request
```http
GET /api/staff/1/schedule-management/vehicle/10/history
```

### Response
Tất cả lịch hẹn của xe ID 10

---

## 🔐 Schedule Status Flow

```
PENDING → CONFIRMED → IN_PROGRESS → DONE
   ↓
CANCELLED
```

### Status Meanings:
- **PENDING** - Chờ xác nhận
- **CONFIRMED** - Đã xác nhận
- **IN_PROGRESS** - Đang bảo trì
- **DONE** - Hoàn thành
- **CANCELLED** - Đã hủy

---

## ⚠️ Business Rules

1. **Tạo lịch mới:**
   - Xe phải thuộc về khách hàng
   - Time slot phải cùng service center
   - Ngày hẹn phải trong tương lai

2. **Cập nhật lịch:**
   - Chỉ cập nhật được PENDING hoặc CONFIRMED
   - Ngày hẹn phải trong tương lai

3. **Xác nhận lịch:**
   - Chỉ confirm được PENDING

4. **Hủy lịch:**
   - Không hủy được IN_PROGRESS hoặc DONE

5. **Staff chỉ quản lý lịch của service center mình**

---

## 🧪 Testing với Postman

### Collection có thể import:
```json
{
  "info": {
    "name": "Staff Schedule Management",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Schedule",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"customerId\": 1,\n  \"vehicleId\": 1,\n  \"packageId\": 1,\n  \"scheduledDate\": \"2025-10-20T09:00:00\",\n  \"slotId\": 1\n}",
          "options": {
            "raw": {
              "language": "json"
            }
          }
        },
        "url": {
          "raw": "http://localhost:8080/api/staff/1/schedule-management",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "staff", "1", "schedule-management"]
        }
      }
    }
  ]
}
```

---

## 📝 Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-10-14T13:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Vehicle does not belong to this customer"
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-10-14T13:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Schedule not found"
}
```

---

## 🚀 Next Steps

- [ ] Thêm authentication/authorization
- [ ] Thêm pagination cho search
- [ ] Thêm sorting options
- [ ] Thêm notification system
- [ ] Thêm validation chi tiết hơn
- [ ] Thêm unit tests
