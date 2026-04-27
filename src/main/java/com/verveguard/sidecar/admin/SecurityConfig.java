package com.verveguard.sidecar.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery) protection
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define the exact rules for your endpoints
                .authorizeHttpRequests(auth -> auth
                        // The gateway is completely open for high-speed POS requests
                        .requestMatchers("/api/v1/transactions/**").permitAll()

                        // NEW: Open the temporary backdoor so you can actually get a token in Postman
                        .requestMatchers("/api/v1/admin/generate-token").permitAll()

                        // The actual dashboard remains strictly locked down.
                        // Any other /admin/ request MUST have the Bearer token.
                        .requestMatchers("/api/v1/admin/**").authenticated()
                )

                // 3. Tell Spring NOT to save sessions in memory (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Put your custom Security Guard at the front door before standard checks
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}