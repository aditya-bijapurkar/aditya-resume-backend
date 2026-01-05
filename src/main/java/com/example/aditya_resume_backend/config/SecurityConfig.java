package com.example.aditya_resume_backend.config;

import com.example.aditya_resume_backend.config.filters.JwtAuthFilter;
import com.example.aditya_resume_backend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JWTUtils jwtUtil() {
        return new JWTUtils(jwtSecret);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(JWTUtils jwtUtils) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtUtils));
        registration.addUrlPatterns("/api/schedule/meet/fetch/*");
        return registration;
    }

}
