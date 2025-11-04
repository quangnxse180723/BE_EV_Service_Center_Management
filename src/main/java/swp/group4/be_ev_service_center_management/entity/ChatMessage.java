package swp.group4.be_ev_service_center_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * NOTE: Entity ChatMessage - Bảng lưu trữ tin nhắn chat
 *
 * Mục đích:
 * - Lưu trữ tất cả tin nhắn trong hệ thống chat 1:1
 * - Mỗi tin nhắn thuộc về một conversation (giữa customer và staff)
 * - Track sender và receiver để hiển thị UI đúng
 *
 * Quan hệ:
 * - Many-to-One với Conversation: nhiều messages trong 1 conversation
 * - Many-to-One với Account (sender): người gửi
 * - Many-to-One với Account (receiver): người nhận
 */
@Entity
@Table(name = "chatmessage")  // NOTE: Tên bảng trong MySQL
@Data  // NOTE: Lombok tự generate getters/setters/toString/equals/hashCode
@NoArgsConstructor  // NOTE: Constructor không tham số (required by JPA)
@AllArgsConstructor  // NOTE: Constructor với tất cả tham số
public class ChatMessage {
    
    // NOTE: Primary key - Auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;
    
    // NOTE: Foreign key đến bảng conversation
    // Mỗi message thuộc về một conversation (cuộc hội thoại giữa customer-staff)
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    // NOTE: Foreign key đến bảng account - người gửi tin nhắn
    // Có thể là customer hoặc staff
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;
    
    // NOTE: Foreign key đến bảng account - người nhận tin nhắn
    // Nếu sender là customer thì receiver là staff, và ngược lại
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;
    
    // NOTE: Nội dung tin nhắn - tối đa 1000 ký tự
    @Column(name = "content", length = 1000)
    private String content;
    
    // NOTE: Timestamp tự động khi tạo message - không thể update
    // Hibernate tự động set giá trị khi insert
    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
}
