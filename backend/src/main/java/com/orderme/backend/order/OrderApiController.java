package com.orderme.backend.order;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5500")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/status")
    public void updateOrderStatus(@PathVariable Integer id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
    }
} 