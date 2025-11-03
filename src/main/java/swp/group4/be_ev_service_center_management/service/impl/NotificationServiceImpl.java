package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.response.NotificationDTO;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.MaintenanceRecord;
import swp.group4.be_ev_service_center_management.entity.Notification;
import swp.group4.be_ev_service_center_management.repository.AccountRepository;
import swp.group4.be_ev_service_center_management.repository.NotificationRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @Override
    public Notification createNotification(Account sender, Account receiver, MaintenanceRecord relatedRecord,
                                           String type, String title, String message) {
        Notification n = new Notification();
        n.setSender(sender);
        n.setReceiver(receiver);
        n.setRelatedRecord(relatedRecord);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setIsRead(false);
        // createdAt handled by @CreationTimestamp in entity; set explicitly if needed
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    @Override
    public Page<NotificationDTO> getNotificationsForAccount(String receiverEmail, int page, int size) {
        Optional<Account> receiverOpt = accountRepository.findByEmail(receiverEmail);
        if (receiverOpt.isEmpty()) return new PageImpl<>(List.of());
        Account receiver = receiverOpt.get();
        Pageable pageable = PageRequest.of(page, size <= 0 ? 10 : size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> p = notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver, pageable);
        return p.map(this::toDto);
    }

    @Override
    public long countUnread(String receiverEmail) {
        Optional<Account> receiverOpt = accountRepository.findByEmail(receiverEmail);
        return receiverOpt.map(account -> notificationRepository.countByReceiverAndIsReadFalse(account)).orElse(0L);
    }

    @Override
    public boolean markAsRead(Integer notificationId, String receiverEmail) {
        Optional<Notification> o = notificationRepository.findById(notificationId);
        if (o.isEmpty()) return false;
        Notification n = o.get();
        if (n.getReceiver() == null || n.getReceiver().getEmail() == null) return false;
        if (!n.getReceiver().getEmail().equals(receiverEmail)) return false;
        n.setIsRead(true);
        notificationRepository.save(n);
        return true;
    }

    @Override
    public int markAllAsRead(String receiverEmail) {
        Optional<Account> receiverOpt = accountRepository.findByEmail(receiverEmail);
        if (receiverOpt.isEmpty()) return 0;
        Account receiver = receiverOpt.get();
        // lấy một trang lớn để mark all; nếu dữ liệu lớn cần paging
        Page<Notification> page = notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver, PageRequest.of(0, 1000));
        List<Notification> unread = page.stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).collect(Collectors.toList());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        return unread.size();
    }

    private NotificationDTO toDto(Notification n) {
        NotificationDTO d = new NotificationDTO();
        d.setNotificationId(n.getNotificationId());
        d.setSenderId(n.getSender() != null ? n.getSender().getAccountId() : null);
        d.setReceiverId(n.getReceiver() != null ? n.getReceiver().getAccountId() : null);
        d.setRelatedRecordId(n.getRelatedRecord() != null ? n.getRelatedRecord().getRecordId() : null);
        d.setType(n.getType());
        d.setTitle(n.getTitle());
        d.setMessage(n.getMessage());
        d.setCreatedAt(n.getCreatedAt());
        d.setIsRead(n.getIsRead());
        return d;
    }
}