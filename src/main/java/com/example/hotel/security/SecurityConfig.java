package com.example.hotel.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Публичные endpoints и статика
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()

                        // Сотрудники — только Администратор
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")

                        // Платёжные карты и Счета — только чтение для всех, изменение для ADMIN и MANAGER
                        .requestMatchers(HttpMethod.GET, "/api/payment-cards/**", "/api/invoice-items/**")
                        .hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
                        .requestMatchers("/api/payment-cards/**", "/api/invoice-items/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Номера и Детали номеров — только чтение для всех, изменение для ADMIN и MANAGER
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**", "/api/room-details/**")
                        .hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
                        .requestMatchers("/api/rooms/**", "/api/room-details/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // Клиенты, Регистрации, Услуги — все операции для всех ролей
                        .requestMatchers(
                                "/api/clients/**",
                                "/api/registrations/**",
                                "/api/services/**",
                                "/api/service-provisions/**"
                        ).hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")

                        // Всё остальное API — только авторизованные
                        .requestMatchers("/api/**").authenticated()

                        // Остальные пути (статика) — разрешены
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Если запрос к API — возвращаем JSON
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write(
                                        "{\"message\":\"Требуется авторизация\"}"
                                );
                            } else {
                                // Для не-API запросов — редирект на главную
                                response.sendRedirect("/");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write(
                                        "{\"message\":\"Доступ запрещён\"}"
                                );
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}