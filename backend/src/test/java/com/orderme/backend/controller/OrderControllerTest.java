package com.orderme.backend.controller;

import com.orderme.backend.order.Order;
import com.orderme.backend.order.OrderRepository;
import com.orderme.backend.order.OrderCacheService;
import com.orderme.backend.order.OrderStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private OrderCacheService orderCacheService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderRepository, orderStatusService, orderCacheService);
    }

    @Test
    void getAllOrders_ShouldReturnCachedOrders() {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        when(orderCacheService.getCachedOrders()).thenReturn(expectedOrders);

        // Act
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedOrders, response.getBody());
        verify(orderCacheService).getCachedOrders();
        verifyNoInteractions(orderRepository);
    }

    @Test
    void getAllOrders_ShouldFetchFromDatabaseWhenCacheEmpty() {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        when(orderCacheService.getCachedOrders()).thenReturn(null);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // Act
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedOrders, response.getBody());
        verify(orderCacheService).getCachedOrders();
        verify(orderRepository).findAll();
        verify(orderCacheService).cacheOrders(expectedOrders);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatusAndInvalidateCache() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "In Preparation";
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(orderRepository).existsById(orderId);
        verify(orderRepository).updateOrderStatus(orderId, newStatus);
        verify(orderStatusService).updateOrderStatus(orderId, newStatus);
        verify(orderCacheService).invalidateCache();
    }

    @Test
    void updateOrderStatus_ShouldReturnNotFound_WhenOrderDoesNotExist() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "In Preparation";
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Act
        ResponseEntity<?> response = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(orderRepository).existsById(orderId);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderStatusService);
        verifyNoInteractions(orderCacheService);
    }

    @Test
    void createOrder_ShouldSaveAndCacheOrder() {
        // Arrange
        Order order = createSampleOrders().get(0);
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order savedOrder = orderController.createOrder(order);

        // Assert
        assertNotNull(savedOrder);
        verify(orderRepository).save(order);
        verify(orderStatusService).cacheOrderStatus(order.getOrderid(), order.getStatus());
    }

    private List<Order> createSampleOrders() {
        Order order1 = new Order();
        order1.setOrderid(1);
        order1.setTableid("table1");
        order1.setStatus("Pending");
        order1.setTotalcost(new BigDecimal("25.99"));
        order1.setMealname("Burger");
        order1.setCreatedat(LocalDateTime.now());

        Order order2 = new Order();
        order2.setOrderid(2);
        order2.setTableid("table2");
        order2.setStatus("In Preparation");
        order2.setTotalcost(new BigDecimal("15.99"));
        order2.setMealname("Pizza");
        order2.setCreatedat(LocalDateTime.now());

        return Arrays.asList(order1, order2);
    }
} 