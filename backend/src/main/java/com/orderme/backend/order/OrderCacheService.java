package com.orderme.backend.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrderCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String ORDERS_CACHE_KEY = "orders:all";
    private static final long CACHE_TTL = 30; // Cache for 30 seconds

    public OrderCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Order> getCachedOrders() {
        try {
            String cachedOrders = redisTemplate.opsForValue().get(ORDERS_CACHE_KEY);
            if (cachedOrders != null) {
                return objectMapper.readValue(cachedOrders, new TypeReference<List<Order>>() {});
            }
        } catch (Exception e) {
            // Log error but don't throw - we'll fetch from DB instead
            System.err.println("Error reading from Redis cache: " + e.getMessage());
        }
        return null;
    }

    public void cacheOrders(List<Order> orders) {
        try {
            String ordersJson = objectMapper.writeValueAsString(orders);
            redisTemplate.opsForValue().set(ORDERS_CACHE_KEY, ordersJson, CACHE_TTL, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Log error but don't throw - caching is not critical
            System.err.println("Error writing to Redis cache: " + e.getMessage());
        }
    }

    public void invalidateCache() {
        redisTemplate.delete(ORDERS_CACHE_KEY);
    }
} 