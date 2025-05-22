package com.orderme.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenService tokenService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtils, tokenService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenValidToken() throws Exception {
        // Arrange
        String token = "valid.token.here";
        String username = "john.doe";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtils.extractUsername(token)).thenReturn(username);
        when(tokenService.validateToken(username, token)).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenNoToken() throws Exception {
        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenInvalidToken() throws Exception {
        // Arrange
        String token = "invalid.token.here";
        String username = "john.doe";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtils.extractUsername(token)).thenReturn(username);
        when(tokenService.validateToken(username, token)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
} 