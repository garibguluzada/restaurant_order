package com.orderme.backend.controller;

import com.orderme.backend.table.RestaurantTable;
import com.orderme.backend.table.TableRepository;
import com.orderme.backend.security.JwtUtils;
import com.orderme.backend.security.TokenService;
import com.orderme.backend.staff.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private TableRepository tableRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenService tokenService;

    @Mock
    private StaffService staffService;

    @Mock
    private HttpServletResponse response;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(jwtUtils, tokenService, staffService, tableRepository);
    }

    @Test
    void loginWithId_ShouldReturnTokenForValidTable() {
        // Arrange
        String tableId = "table1";
        String expectedToken = "jwt.token.here";
        RestaurantTable table = new RestaurantTable();
        table.setTableid(tableId);
        table.setTablenum(1);

        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(jwtUtils.generateToken(any())).thenReturn(expectedToken);

        // Act
        Map<String, String> body = new HashMap<>();
        body.put("id", tableId);
        ResponseEntity<?> response = authController.idLogin(body, this.response);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(tableRepository).findById(tableId);
        verify(jwtUtils).generateToken(any());
        verify(tokenService).storeToken(eq("client:" + tableId), eq(expectedToken));
    }

    @Test
    void loginWithId_ShouldReturnUnauthorizedForInvalidTable() {
        // Arrange
        String tableId = "nonexistent";
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // Act
        Map<String, String> body = new HashMap<>();
        body.put("id", tableId);
        ResponseEntity<?> response = authController.idLogin(body, this.response);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(tableRepository).findById(tableId);
        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(tokenService);
    }

    @Test
    void logout_ShouldRemoveTokenFromRedis() {
        // Arrange
        String token = "jwt.token.here";
        Claims claims = mock(Claims.class);
        when(claims.get("username")).thenReturn("testuser");
        when(jwtUtils.getClaimsFromToken(token)).thenReturn(claims);

        // Act
        ResponseEntity<?> response = authController.logout("Bearer " + token, token, this.response);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(tokenService).removeToken("testuser");
    }
} 