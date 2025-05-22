package com.orderme.backend.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setPassword("password123");
        user.setRole("ROLE_USER");

        assertEquals(1L, user.getId());
        assertEquals("john.doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void testUserEquality() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john.doe");
        user1.setPassword("password123");
        user1.setRole("ROLE_USER");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("john.doe");
        user2.setPassword("password123");
        user2.setRole("ROLE_USER");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
} 