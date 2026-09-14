package com.example.ecommerce;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. ALL STATIC HTML PAGES
                .requestMatchers(
                    "/", 
                    "/index.html", 
                    "/seller/**", 
                    "/seller/portal.html",
                    "/internal/**", 
                    "/internal/ops.html",
                    "/static/**", 
                    "/error",
                    "/favicon.ico"
                ).permitAll()

                // 2. PUBLIC APIS & AUTH
                .requestMatchers("/api/v1/auth/**", "/api/v1/internal/midway/**").permitAll()
                .requestMatchers("/api/v1/products", "/api/v1/products/*", "/api/v1/products/*/related", "/api/v1/products/*/reviews").permitAll()
                .requestMatchers("/api/v1/cart/**").permitAll()
                
                // 3. ORDERS APIS (Allow checkout & history)
                .requestMatchers("/api/v1/orders/**").permitAll()

                // 4. RESTRICTED SELLER & ADMIN APIS
                .requestMatchers("/api/v1/products/seller/**").hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}