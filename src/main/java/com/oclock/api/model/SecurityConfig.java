package com.oclock.api.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Desativa CSRF (necessário para APIs REST)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Libera todos os endpoints
                );
        return http.build();
    }
}
