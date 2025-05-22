package com.orderme.backend.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

class OrderTest {

    @Test
    void testOrderCreation() {
        Order order = new Order();
        order.setOrderid(1);
        order.setTableid("5");
        order.setStatus("PENDING");
        order.setCreatedat(LocalDateTime.now());
        order.setTotalcost(new BigDecimal("29.99"));
        order.setMealname("Test Meal");

        assertEquals(1, order.getOrderid());
        assertEquals("5", order.getTableid());
        assertEquals("PENDING", order.getStatus());
        assertNotNull(order.getCreatedat());
        assertEquals(new BigDecimal("29.99"), order.getTotalcost());
        assertEquals("Test Meal", order.getMealname());
    }

    @Test
    void testOrderEquality() {
        LocalDateTime now = LocalDateTime.now();
        
        Order order1 = new Order();
        order1.setOrderid(1);
        order1.setTableid("5");
        order1.setStatus("PENDING");
        order1.setCreatedat(now);
        order1.setTotalcost(new BigDecimal("29.99"));
        order1.setMealname("Test Meal");

        Order order2 = new Order();
        order2.setOrderid(1);
        order2.setTableid("5");
        order2.setStatus("PENDING");
        order2.setCreatedat(now);
        order2.setTotalcost(new BigDecimal("29.99"));
        order2.setMealname("Test Meal");

        assertTrue(order1.equals(order2));
        assertEquals(order1.hashCode(), order2.hashCode());
    }
} 