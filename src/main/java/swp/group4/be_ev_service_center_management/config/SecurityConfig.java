package swp.group4.be_ev_service_center_management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import swp.group4.be_ev_service_center_management.security.JwtFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Để trống, dùng CORS mặc định từ WebConfig
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - không cần authentication
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/payment/vnpay/callback").permitAll()  // VNPay callback không cần token
                .requestMatchers("/api/payment/vnpay/return").permitAll()    // VNPay return URL không cần token
                .requestMatchers("/ws/**").permitAll()  // WebSocket endpoint
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/package-checklist-items").permitAll()  // Bảng giá - xem được khi chưa đăng nhập
                .requestMatchers("/api/parts").permitAll()  // Danh sách phụ tùng - xem được khi chưa đăng nhập


                // Protected endpoints - YÊU CẦU JWT
                .requestMatchers("/api/chat/**").authenticated()  // Chat APIs cần authentication
                .requestMatchers("/api/**").authenticated()  // Tất cả APIs khác cũng cần

                // QUAN TRỌNG: anyRequest() phải đặt cuối cùng
                .anyRequest().authenticated() // Mặc định: cần authentication
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
        // NOTE: Thêm JWT filter TRƯỚC UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
