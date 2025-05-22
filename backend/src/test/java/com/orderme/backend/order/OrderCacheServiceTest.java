package com.orderme.backend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private OrderCacheService orderCacheService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        orderCacheService = new OrderCacheService(redisTemplate, objectMapper);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getCachedOrders_ShouldReturnCachedOrders() throws Exception {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        String ordersJson = objectMapper.writeValueAsString(expectedOrders);
        when(valueOperations.get("orders:all")).thenReturn(ordersJson);

        // Act
        List<Order> actualOrders = orderCacheService.getCachedOrders();

        // Assert
        assertNotNull(actualOrders);
        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0).getOrderid(), actualOrders.get(0).getOrderid());
        verify(valueOperations).get("orders:all");
    }

    @Test
    void cacheOrders_ShouldCacheOrders() throws Exception {
        // Arrange
        List<Order> orders = createSampleOrders();
        String ordersJson = objectMapper.writeValueAsString(orders);

        // Act
        orderCacheService.cacheOrders(orders);

        // Assert
        verify(valueOperations).set(eq("orders:all"), eq(ordersJson), eq(30L), any());
    }

    @Test
    void invalidateCache_ShouldDeleteCache() {
        // Act
        orderCacheService.invalidateCache();

        // Assert
        verify(redisTemplate).delete("orders:all");
    }

    private List<Order> createSampleOrders() {
        Order order1 = new Order();
        order1.setOrderid(1);
        order1.setTableid("T5");
        order1.setStatus("PENDING");
        order1.setCreatedat(LocalDateTime.now());
        order1.setTotalcost(new BigDecimal("29.99"));
        order1.setMealname("Burger");

        Order order2 = new Order();
        order2.setOrderid(2);
        order2.setTableid("T6");
        order2.setStatus("COMPLETED");
        order2.setCreatedat(LocalDateTime.now());
        order2.setTotalcost(new BigDecimal("39.99"));
        order2.setMealname("Pizza");

        return Arrays.asList(order1, order2);
    }
} 