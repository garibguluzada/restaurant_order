package com.orderme.backend.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "john.doe");
        claims.put("role", "waiter");

        // Act
        String token = jwtUtils.generateToken(claims);

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtils.isTokenValid(token));
        Claims extractedClaims = jwtUtils.getClaimsFromToken(token);
        assertEquals("john.doe", extractedClaims.get("username"));
        assertEquals("waiter", extractedClaims.get("role"));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtils.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getClaimsFromToken_ShouldReturnCorrectClaims() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "john.doe");
        claims.put("role", "waiter");
        String token = jwtUtils.generateToken(claims);

        // Act
        Claims extractedClaims = jwtUtils.getClaimsFromToken(token);

        // Assert
        assertEquals("john.doe", extractedClaims.get("username"));
        assertEquals("waiter", extractedClaims.get("role"));
    }
} 