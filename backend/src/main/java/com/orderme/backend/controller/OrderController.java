package com.orderme.backend.controller;

import com.orderme.backend.order.Order;
import com.orderme.backend.order.OrderRepository;
import com.orderme.backend.order.OrderCacheService;
import com.orderme.backend.order.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true", allowedHeaders = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS })
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final OrderCacheService orderCacheService;

    @Autowired
    public OrderController(OrderRepository orderRepository, 
                          OrderStatusService orderStatusService,
                          OrderCacheService orderCacheService) {
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
        this.orderCacheService = orderCacheService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);
        orderStatusService.cacheOrderStatus(savedOrder.getOrderid(), savedOrder.getStatus());
        logger.info("Created new order {} with status {} and cached in Redis", savedOrder.getOrderid(), savedOrder.getStatus());
        return savedOrder;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        // Try to get orders from cache first
        List<Order> cachedOrders = orderCacheService.getCachedOrders();
        if (cachedOrders != null) {
            return ResponseEntity.ok(cachedOrders);
        }

        // If not in cache, get from database
        List<Order> orders = orderRepository.findAll();
        
        // Cache the orders for future requests
        orderCacheService.cacheOrders(orders);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<Integer, String>> getAllOrderStatuses() {
        Map<Integer, String> statuses = orderStatusService.getAllOrderStatuses();
        return ResponseEntity.ok(statuses);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Integer id, @RequestBody Order updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            order.setTableid(updatedOrder.getTableid());
            order.setStatus(updatedOrder.getStatus());
            order.setTotalcost(updatedOrder.getTotalcost());
            order.setMealname(updatedOrder.getMealname());
            Order savedOrder = orderRepository.save(order);
            orderStatusService.cacheOrderStatus(savedOrder.getOrderid(), savedOrder.getStatus());
            logger.info("Updated order {} with new status {} and cached in Redis", savedOrder.getOrderid(), savedOrder.getStatus());
            return savedOrder;
        }).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody String newStatus) {
        try {
            // Remove quotes if present
            newStatus = newStatus.replaceAll("^\"|\"$", "");
            
            // Validate order exists
            if (!orderRepository.existsById(orderId)) {
                return ResponseEntity.status(404).body("Order not found with id: " + orderId);
            }

            // Update order status
            orderRepository.updateOrderStatus(orderId, newStatus);
            orderStatusService.updateOrderStatus(orderId, newStatus);
            orderCacheService.invalidateCache();
            
            logger.info("Successfully updated order {} status to {}", orderId, newStatus);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating order {} status: {}", orderId, e.getMessage());
            return ResponseEntity.status(500).body("Failed to update order status: " + e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    public String getOrderStatus(@PathVariable Integer id) {
        // Try to get status from Redis first
        String cachedStatus = orderStatusService.getOrderStatus(id);
        if (cachedStatus != null) {
            logger.info("Retrieved order {} status {} from Redis cache", id, cachedStatus);
            return cachedStatus;
        }

        // If not in Redis, get from database and cache it
        logger.info("Order {} status not found in Redis, fetching from database", id);
        return orderRepository.findById(id)
            .map(order -> {
                orderStatusService.cacheOrderStatus(order.getOrderid(), order.getStatus());
                logger.info("Retrieved order {} status {} from database and cached in Redis", order.getOrderid(), order.getStatus());
                return order.getStatus();
            })
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}
