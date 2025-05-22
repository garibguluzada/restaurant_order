package com.orderme.backend.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderApiControllerTest {

    @Mock
    private OrderService orderService;

    private OrderApiController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderApiController(orderService);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Arrange
        List<Order> expectedOrders = createSampleOrders();
        when(orderService.getAllOrders()).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderController.getAllOrders();

        // Assert
        assertEquals(expectedOrders, actualOrders);
        verify(orderService).getAllOrders();
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Arrange
        Order expectedOrder = createSampleOrders().get(0);
        when(orderService.getOrderById(1)).thenReturn(expectedOrder);

        // Act
        Order actualOrder = orderController.getOrderById(1);

        // Assert
        assertNotNull(actualOrder);
        assertEquals(expectedOrder, actualOrder);
        verify(orderService).getOrderById(1);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "COMPLETED";

        // Act
        orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        verify(orderService).updateOrderStatus(orderId, newStatus);
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