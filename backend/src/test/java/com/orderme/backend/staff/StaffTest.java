package com.orderme.backend.staff;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class StaffTest {

    @Test
    void testStaffCreation() {
        Staff staff = new Staff("john.doe", "password123", "waiter");
        staff.setStaffid(1L);
        staff.setCreatedat(LocalDateTime.now());

        assertEquals(1L, staff.getStaffid());
        assertEquals("john.doe", staff.getUsername());
        assertEquals("waiter", staff.getRole());
        assertNotNull(staff.getCreatedat());
    }

    @Test
    void testStaffEquality() {
        Staff staff1 = new Staff("john.doe", "password123", "waiter");
        staff1.setStaffid(1L);
        staff1.setCreatedat(LocalDateTime.now());

        Staff staff2 = new Staff("john.doe", "password123", "waiter");
        staff2.setStaffid(1L);
        staff2.setCreatedat(staff1.getCreatedat());

        assertEquals(staff1, staff2);
        assertEquals(staff1.hashCode(), staff2.hashCode());
    }
} 