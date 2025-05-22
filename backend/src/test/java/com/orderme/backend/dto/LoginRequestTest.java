package com.orderme.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequestCreation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john.doe");
        request.setPassword("password123");

        assertEquals("john.doe", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testLoginRequestEquality() {
        LoginRequest request1 = new LoginRequest();
        request1.setUsername("john.doe");
        request1.setPassword("password123");

        LoginRequest request2 = new LoginRequest();
        request2.setUsername("john.doe");
        request2.setPassword("password123");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
} 