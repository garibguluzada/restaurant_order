package com.orderme.backend.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCacheService orderCacheService;

    @Mock
    private OrderStatusService orderStatusService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, orderCacheService, orderStatusService);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        when(orderCacheService.getCachedOrders()).thenReturn(null);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getAllOrders();

        // Assert
        assertEquals(expectedOrders, actualOrders);
        verify(orderCacheService).getCachedOrders();
        verify(orderRepository).findAll();
        verify(orderCacheService).cacheOrders(expectedOrders);
    }

    @Test
    void getAllOrders_ShouldReturnCachedOrders() {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        when(orderCacheService.getCachedOrders()).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getAllOrders();

        // Assert
        assertEquals(expectedOrders, actualOrders);
        verify(orderCacheService).getCachedOrders();
        verify(orderRepository, never()).findAll();
        verify(orderCacheService, never()).cacheOrders(any());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Arrange
        Order expectedOrder = createSampleOrders().get(0);
        when(orderRepository.findById(1)).thenReturn(Optional.of(expectedOrder));

        // Act
        Order actualOrder = orderService.getOrderById(1);

        // Assert
        assertNotNull(actualOrder);
        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository).findById(1);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "COMPLETED";

        // Act
        orderService.updateOrderStatus(orderId, newStatus);

        // Assert
        verify(orderRepository).updateOrderStatus(orderId, newStatus);
        verify(orderStatusService).updateOrderStatus(orderId, newStatus);
        verify(orderCacheService).invalidateCache();
    }

    private List<Order> createSampleOrders() {
        LocalDateTime now = LocalDateTime.now();
        
        Order order1 = new Order();
        order1.setOrderid(1);
        order1.setTableid("T5");
        order1.setStatus("PENDING");
        order1.setCreatedat(now);
        order1.setTotalcost(new BigDecimal("29.99"));
        order1.setMealname("Burger");

        Order order2 = new Order();
        order2.setOrderid(2);
        order2.setTableid("T6");
        order2.setStatus("COMPLETED");
        order2.setCreatedat(now);
        order2.setTotalcost(new BigDecimal("39.99"));
        order2.setMealname("Pizza");

        return Arrays.asList(order1, order2);
    }
} 