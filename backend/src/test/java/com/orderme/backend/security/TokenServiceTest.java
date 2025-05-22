package com.orderme.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void storeToken_ShouldStoreTokenInRedis() {
        // Arrange
        String username = "john.doe";
        String token = "test.token.here";

        // Act
        tokenService.storeToken(username, token);

        // Assert
        verify(valueOperations).set(eq("jwt:john.doe"), eq(token), eq(24L), any());
    }

    @Test
    void getToken_ShouldReturnStoredToken() {
        // Arrange
        String username = "john.doe";
        String expectedToken = "test.token.here";
        when(valueOperations.get("jwt:john.doe")).thenReturn(expectedToken);

        // Act
        String actualToken = tokenService.getToken(username);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(valueOperations).get("jwt:john.doe");
    }

    @Test
    void removeToken_ShouldDeleteTokenFromRedis() {
        // Arrange
        String username = "john.doe";

        // Act
        tokenService.removeToken(username);

        // Assert
        verify(redisTemplate).delete("jwt:john.doe");
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        String username = "john.doe";
        String token = "test.token.here";
        when(valueOperations.get("jwt:john.doe")).thenReturn(token);

        // Act
        boolean isValid = tokenService.validateToken(username, token);

        // Assert
        assertTrue(isValid);
        verify(valueOperations).get("jwt:john.doe");
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        // Arrange
        String username = "john.doe";
        String storedToken = "test.token.here";
        String providedToken = "different.token";
        when(valueOperations.get("jwt:john.doe")).thenReturn(storedToken);

        // Act
        boolean isValid = tokenService.validateToken(username, providedToken);

        // Assert
        assertFalse(isValid);
        verify(valueOperations).get("jwt:john.doe");
    }
} 