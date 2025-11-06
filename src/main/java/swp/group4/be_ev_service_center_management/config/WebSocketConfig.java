package swp.group4.be_ev_service_center_management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import swp.group4.be_ev_service_center_management.security.JwtUtil;

import java.util.ArrayList;

/**
 * NOTE: WebSocket Configuration Class
 *
 * Mục đích:
 * - Cấu hình WebSocket để hỗ trợ real-time chat giữa customer và staff
 * - Tích hợp JWT authentication để bảo mật WebSocket connection
 * - Định nghĩa các endpoint và message broker cho STOMP protocol
 *
 * Cách hoạt động:
 * 1. Client kết nối đến endpoint /ws với JWT token trong header
 * 2. Server verify JWT token khi CONNECT
 * 3. Client subscribe vào /topic/conversation/{conversationId} để nhận tin nhắn
 * 4. Client gửi tin nhắn đến /app/chat.send
 * 5. Server broadcast tin nhắn đến tất cả subscribers trong conversation
 */
@Configuration
@EnableWebSocketMessageBroker  // NOTE: Enable WebSocket message handling, backed by a message broker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;  // NOTE: Inject JwtUtil để verify JWT token từ WebSocket connection

    /**
     * NOTE: Cấu hình Message Broker
     *
     * Message Broker xử lý việc routing messages giữa client và server:
     * - /topic: prefix cho broadcast messages (nhiều người nhận)
     * - /app: prefix cho messages gửi đến server (application destination)
     *
     * Ví dụ flow:
     * 1. Client gửi message đến: /app/chat.send
     * 2. Server xử lý trong @MessageMapping("/chat.send")
     * 3. Server broadcast đến: /topic/conversation/{conversationId}
     * 4. Tất cả clients subscribe /topic/conversation/{conversationId} sẽ nhận được
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // NOTE: Enable simple in-memory message broker với prefix /topic
        // Tất cả messages gửi đến /topic/* sẽ được broadcast đến subscribers
        config.enableSimpleBroker("/topic");

        // NOTE: Set application destination prefix là /app
        // Client gửi messages đến /app/* sẽ được route đến @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * NOTE: Đăng ký STOMP endpoints
     *
     * STOMP (Simple Text Oriented Messaging Protocol) là protocol cho WebSocket
     * Endpoint /ws là điểm kết nối WebSocket cho clients
     *
     * SockJS fallback: Nếu WebSocket không được hỗ trợ (browser cũ, firewall),
     * SockJS sẽ tự động fallback sang long-polling hoặc streaming
     *
     * CORS: Cho phép tất cả origins kết nối (trong production nên restrict)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // NOTE: Đăng ký endpoint /ws cho WebSocket connections
        registry.addEndpoint("/ws")  // Client kết nối: ws://localhost:8080/ws
                .setAllowedOriginPatterns("*")  // NOTE: Cho phép CORS từ mọi origin (dev only)
                .withSockJS();  // NOTE: Enable SockJS fallback cho browsers không hỗ trợ WebSocket
    }

    /**
     * NOTE: Cấu hình Channel Interceptor để xác thực JWT
     *
     * Interceptor này chặn tất cả messages đến server và verify JWT token
     * Chỉ cho phép kết nối nếu JWT token hợp lệ
     *
     * Flow JWT authentication trong WebSocket:
     * 1. Client gửi CONNECT frame với JWT token trong header "Authorization"
     * 2. Interceptor lấy token từ header
     * 3. JwtUtil verify token và extract username (email)
     * 4. Nếu valid: set authentication vào SecurityContext và cho phép kết nối
     * 5. Nếu invalid: reject connection
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            /**
             * NOTE: Intercept messages trước khi gửi đến handler
             *
             * Kiểm tra JWT token trong CONNECT command
             * Extract user info và set vào SecurityContext để các @MessageMapping có thể access
             */
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // NOTE: Chỉ verify JWT khi client CONNECT (kết nối lần đầu)
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // NOTE: Lấy JWT token từ header "Authorization"
                    // Client phải gửi: {"Authorization": "Bearer <token>"}
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        // NOTE: Extract token (bỏ prefix "Bearer ")
                        String token = authHeader.substring(7);

                        try {
                            // NOTE: Verify token và extract username (email)
                            String username = jwtUtil.extractUsername(token);

                            // NOTE: Validate token (check expiration, signature)
                            if (jwtUtil.validateToken(token, username)) {
                                // NOTE: Tạo Authentication object và set vào SecurityContext
                                // Điều này cho phép @MessageMapping methods access user info qua Principal
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                accessor.setUser(authentication);

                                // NOTE: Log successful authentication (có thể bỏ trong production)
                                System.out.println("WebSocket authenticated user: " + username);
                            } else {
                                // NOTE: Token invalid - reject connection
                                throw new RuntimeException("Invalid JWT token");
                            }
                        } catch (Exception e) {
                            // NOTE: Nếu có lỗi trong quá trình verify token - reject connection
                            System.err.println("WebSocket authentication failed: " + e.getMessage());
                            throw new RuntimeException("WebSocket authentication failed");
                        }
                    } else {
                        // NOTE: Không có token - reject connection
                        throw new RuntimeException("Missing JWT token in WebSocket connection");
                    }
                }

                return message;
            }
        });
    }
}
