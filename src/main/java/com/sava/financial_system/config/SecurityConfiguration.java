package com.sava.financial_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/accounts/**").permitAll()
                        .requestMatchers("/balances/**").permitAll()
                        .requestMatchers("/transactions/**").permitAll()
                        .requestMatchers("/transfers/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}