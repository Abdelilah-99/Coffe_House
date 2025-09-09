package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/register", "/login", "/api/auth/register",
                                                                "/api/auth/login")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                .formLogin(form -> form.disable())

                                .logout(logout -> logout.permitAll())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

                return http.build();
        }

}