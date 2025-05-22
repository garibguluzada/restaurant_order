package com.orderme.backend.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private OrderStatusService orderStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderStatusService = new OrderStatusService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void cacheOrderStatus_ShouldCacheStatus() {
        // Arrange
        Integer orderId = 1;
        String status = "PENDING";

        // Act
        orderStatusService.cacheOrderStatus(orderId, status);

        // Assert
        verify(valueOperations).set(eq("order:status:1"), eq(status), eq(1L), any());
        verify(hashOperations).put("order:statuses", "1", status);
    }

    @Test
    void getOrderStatus_ShouldReturnStatus() {
        // Arrange
        Integer orderId = 1;
        String expectedStatus = "PENDING";
        when(valueOperations.get("order:status:1")).thenReturn(expectedStatus);

        // Act
        String actualStatus = orderStatusService.getOrderStatus(orderId);

        // Assert
        assertEquals(expectedStatus, actualStatus);
        verify(valueOperations).get("order:status:1");
    }

    @Test
    void getAllOrderStatuses_ShouldReturnAllStatuses() {
        // Arrange
        Map<Object, Object> rawStatuses = new HashMap<>();
        rawStatuses.put("1", "PENDING");
        rawStatuses.put("2", "COMPLETED");
        when(hashOperations.entries("order:statuses")).thenReturn(rawStatuses);

        // Act
        Map<Integer, String> statuses = orderStatusService.getAllOrderStatuses();

        // Assert
        assertEquals(2, statuses.size());
        assertEquals("PENDING", statuses.get(1));
        assertEquals("COMPLETED", statuses.get(2));
        verify(hashOperations).entries("order:statuses");
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "COMPLETED";

        // Act
        orderStatusService.updateOrderStatus(orderId, newStatus);

        // Assert
        verify(valueOperations).set(eq("order:status:1"), eq(newStatus), eq(1L), any());
        verify(hashOperations).put("order:statuses", "1", newStatus);
    }
} 