package com.orderme.backend.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtUtils jwtUtils;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "table1");

        // Act
        String token = jwtUtils.generateToken(claims);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "table1");
        String token = jwtUtils.generateToken(claims);

        // Act
        boolean isValid = jwtUtils.validateToken(token);

        // Assert
        assertTrue(isValid);
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
        Map<String, Object> expectedClaims = new HashMap<>();
        expectedClaims.put("id", "table1");
        String token = jwtUtils.generateToken(expectedClaims);

        // Act
        Claims actualClaims = jwtUtils.getClaimsFromToken(token);

        // Assert
        assertEquals("table1", actualClaims.get("id"));
    }
} 