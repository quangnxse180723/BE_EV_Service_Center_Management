# 🔴 LỖI: "Receiver not found with ID: 1" - HƯỚNG DẪN FIX NGAY

## ❌ LỖI HIỆN TẠI

```
Request receiverId: 1
Error: Receiver not found with ID: 1
```

**Nguyên nhân:** Frontend đang gửi `receiverId: 1` (là `staffId`), nhưng backend cần `staffAccountId` (account_id của staff).

---

## 🔍 PHÂN TÍCH

Từ logs Frontend bạn gửi:
```javascript
👤 Receiver (Staff) ID: 1  // ← Đây là staffId, KHÔNG PHẢI staffAccountId!
📤 Đang gửi: {receiverId: 1, content: 'ádfsad'}
```

**VẤN ĐỀ:**
- Frontend đang lấy `conversationData.staffId` = 1
- Nhưng phải lấy `conversationData.staffAccountId` = account ID của staff

**Backend cần receiverId là account_id, KHÔNG PHẢI staff_id!**

---

## ✅ GIẢI PHÁP - FIX NGAY TRONG FRONTEND

### **BƯỚC 1: Kiểm tra Response từ Backend**

Khi gọi `POST /api/chat/conversation/start`, backend trả về:

```javascript
{
  "success": true,
  "data": {
    "conversationId": 3,
    "customerId": 3,
    "customerName": "Customer",
    "customerEmail": "customer@evcenter.com",
    "customerAccountId": 17,      // Account ID của customer
    "staffId": 1,                  // ← ĐỪNG DÙNG CÁI NÀY!
    "staffName": "John Staff",
    "staffEmail": "staff1@evcenter.com",
    "staffAccountId": 8            // ← PHẢI DÙNG CÁI NÀY!
  }
}
```

### **BƯỚC 2: Fix Code Frontend**

**❌ SAI - Code hiện tại:**
```javascript
// ChatWidget.jsx - DÒNG NÀO ĐÓ
const receiverId = conversationData.staffId;  // ← SAI! staffId = 1
```

**✅ ĐÚNG - Code phải sửa thành:**
```javascript
// ChatWidget.jsx
const receiverId = conversationData.staffAccountId;  // ← ĐÚNG! accountId của staff
```

### **BƯỚC 3: Thêm Validation**

```javascript
// Sau khi lấy conversation data
const conversationData = result.data;

// KIỂM TRA staffAccountId có tồn tại không
if (!conversationData.staffAccountId) {
  console.error('❌ ERROR: staffAccountId is missing!');
  console.error('Conversation data:', conversationData);
  alert('Error: Cannot get staff account ID. Please contact support.');
  return;
}

// Lưu đúng field
const receiverId = conversationData.staffAccountId;
console.log('✅ Receiver ID (staffAccountId):', receiverId);

// Lưu vào state
setReceiverId(receiverId);
```

### **BƯỚC 4: Fix Hàm Gửi Message**

```javascript
// Hàm sendMessage
const sendMessage = (content) => {
  // KIỂM TRA receiverId
  if (!receiverId) {
    console.error('❌ receiverId is null or undefined!');
    alert('Please start conversation first!');
    return;
  }
  
  console.log('📤 Sending message...');
  console.log('   receiverId:', receiverId);  // Phải là số, không phải 1
  console.log('   content:', content);
  
  const message = {
    receiverId: receiverId,  // ← Đây phải là staffAccountId
    content: content
  };
  
  stompClient.send('/app/chat.send', {}, JSON.stringify(message));
};
```

---

## 📝 CODE FIX HOÀN CHỈNH

```javascript
// ChatWidget.jsx

const initConversation = async () => {
  try {
    const token = localStorage.getItem('token');
    
    const response = await fetch('http://localhost:8080/api/chat/conversation/start', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.success) {
      const conv = result.data;
      
      // LOG ĐỂ DEBUG
      console.log('📦 Conversation data:', conv);
      console.log('staffId:', conv.staffId);
      console.log('staffAccountId:', conv.staffAccountId);
      
      // KIỂM TRA staffAccountId
      if (!conv.staffAccountId) {
        console.error('❌ staffAccountId is missing!');
        console.error('Full data:', conv);
        alert('Error: Cannot get staff account ID');
        return;
      }
      
      // LƯU ĐÚNG FIELD
      const receiverId = conv.staffAccountId;  // ← FIX TẠI ĐÂY!
      
      setConversationId(conv.conversationId);
      setReceiverId(receiverId);  // ← Lưu staffAccountId, không phải staffId
      setStaffName(conv.staffName);
      
      console.log('✅ Receiver ID saved:', receiverId);
      
      // Kết nối WebSocket
      connectWebSocket(token, conv.conversationId);
    }
  } catch (error) {
    console.error('Error starting conversation:', error);
  }
};

const sendMessage = (content) => {
  if (!content.trim()) return;
  
  // KIỂM TRA receiverId
  if (!receiverId) {
    console.error('❌ receiverId is not set!');
    alert('Error: No receiver ID. Please restart chat.');
    return;
  }
  
  console.log('📤 Sending message with receiverId:', receiverId);
  
  const message = {
    receiverId: receiverId,  // ← staffAccountId
    content: content
  };
  
  stompClient.send('/app/chat.send', {}, JSON.stringify(message));
};
```

---

## 🔍 DEBUG CHECKLIST

Sau khi fix, kiểm tra các logs này:

### **1. Console log khi start conversation:**
```
📦 Conversation data: {...}
staffId: 1
staffAccountId: 8  ← PHẢI CÓ GIÁ TRỊ!
✅ Receiver ID saved: 8
```

### **2. Console log khi gửi message:**
```
📤 Sending message with receiverId: 8  ← PHẢI LÀ staffAccountId, không phải 1
```

### **3. Backend logs sẽ thay đổi từ:**
```
❌ TRƯỚC:
Request receiverId: 1
Error: Receiver not found with ID: 1

✅ SAU:
Request receiverId: 8
Receiver: John Staff (STAFF)
✅ Message saved with ID: 1
```

---

## 🎯 TÓM TẮT

**Lỗi:**
- Frontend gửi `receiverId: 1` (staffId)
- Backend tìm account với ID = 1 → Không tìm thấy

**Fix:**
- Frontend phải gửi `receiverId: staffAccountId` (account ID của staff)
- Thay `conversationData.staffId` → `conversationData.staffAccountId`

**Sau khi fix:**
- Backend sẽ tìm thấy receiver
- Message sẽ được lưu thành công
- Broadcast real-time hoạt động

---

## 🚀 RESTART BACKEND

Tôi đã thêm logging vào backend để debug dễ hơn. Restart backend:

```
Ctrl + F2 → Stop
Shift + F10 → Start
```

Sau khi restart, backend logs sẽ hiển thị rõ hơn nếu còn lỗi.

---

## 📞 NẾU VẪN LỖI SAU KHI FIX

Gửi cho tôi:
1. **Frontend console logs** sau khi start conversation
2. **Backend console logs** khi gửi message
3. **Screenshot code** phần lấy receiverId

---

**FIX NGAY BẰNG CÁCH THAY `staffId` → `staffAccountId` TRONG CODE FRONTEND!** ✅

