package com.orderme.backend.order;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;
    private final OrderStatusService orderStatusService;

    public OrderService(OrderRepository orderRepository, 
                       OrderCacheService orderCacheService,
                       OrderStatusService orderStatusService) {
        this.orderRepository = orderRepository;
        this.orderCacheService = orderCacheService;
        this.orderStatusService = orderStatusService;
    }

    public List<Order> getAllOrders() {
        List<Order> cachedOrders = orderCacheService.getCachedOrders();
        if (cachedOrders != null) {
            return cachedOrders;
        }
        List<Order> orders = orderRepository.findAll();
        orderCacheService.cacheOrders(orders);
        return orders;
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateOrderStatus(Integer orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
        orderStatusService.updateOrderStatus(orderId, status);
        orderCacheService.invalidateCache();
    }
} 