package com.orderme.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private JwtUtils jwtUtils;
    private HttpSecurity httpSecurity;
    private SecurityFilterChain filterChain;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = mock(JwtUtils.class);
        securityConfig = new SecurityConfig(jwtUtils);
        httpSecurity = mock(HttpSecurity.class);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));
        filterChain = securityConfig.filterChain(httpSecurity);
    }

    @Test
    void filterChain_ShouldConfigureSecurity() throws Exception {
        // Verify that the security chain is configured
        assertNotNull(filterChain);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(any(), any());
    }

    @Test
    void getTokenFromCookie_ShouldReturnToken() {
        // Create a mock request with a cookie
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("clientToken", "test-token");
        request.setCookies(cookie);

        // Test getting the token
        String token = securityConfig.getTokenFromCookie(request, "clientToken");
        assertEquals("test-token", token);
    }

    @Test
    void getTokenFromCookie_ShouldReturnNull_WhenCookieNotFound() {
        // Create a mock request without the cookie
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("otherToken", "test-token");
        request.setCookies(cookie);

        // Test getting the token
        String token = securityConfig.getTokenFromCookie(request, "clientToken");
        assertNull(token);
    }

    @Test
    void getTokenFromCookie_ShouldReturnNull_WhenNoCookies() {
        // Create a mock request without any cookies
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Test getting the token
        String token = securityConfig.getTokenFromCookie(request, "clientToken");
        assertNull(token);
    }
} 