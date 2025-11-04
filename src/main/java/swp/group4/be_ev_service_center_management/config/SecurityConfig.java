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
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                // NOTE: Public endpoints - không cần JWT
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()  // WebSocket endpoint
                .requestMatchers("/error").permitAll()

                // NOTE: Protected endpoints - YÊU CẦU JWT
                .requestMatchers("/api/chat/**").authenticated()  // Chat APIs cần authentication
                .requestMatchers("/api/**").authenticated()  // Tất cả APIs khác cũng cần

                .anyRequest().authenticated()  // Mặc định: cần authentication
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // NOTE: Thêm JWT filter TRƯỚC UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
