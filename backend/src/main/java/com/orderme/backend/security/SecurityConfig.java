package com.orderme.backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/staff/**").hasRole("STAFF")
                .anyRequest().permitAll()
            );

        http.addFilterBefore((request, response, chain) -> {
            String path = ((HttpServletRequest) request).getRequestURI();
            String token = null;

            if (path.startsWith("/api/client")) {
                token = getTokenFromCookie((HttpServletRequest) request, "clientToken");
            } else {
                token = getTokenFromCookie((HttpServletRequest) request, "staffToken");
            }

            if (token != null && jwtUtils.isTokenValid(token)) {
                var claims = jwtUtils.getClaimsFromToken(token);
                String role = (String) claims.get("role");
                var auth = new UsernamePasswordAuthenticationToken(
                        claims.get("username"),
                        null,
                        role == null ? Collections.emptyList()
                                : Collections.singletonList(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(request, response);
        }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
