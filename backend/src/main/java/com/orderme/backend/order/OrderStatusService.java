package com.orderme.backend.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderStatusService {
    private static final Logger logger = LoggerFactory.getLogger(OrderStatusService.class);
    private static final String KEY_PREFIX = "order:status:";
    private static final long CACHE_DURATION = 1; // 1 hour

    private final RedisTemplate<String, String> redisTemplate;

    public OrderStatusService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheOrderStatus(Integer orderId, String status) {
        String key = KEY_PREFIX + orderId;
        try {
            redisTemplate.opsForValue().set(key, status, CACHE_DURATION, TimeUnit.HOURS);
            redisTemplate.opsForHash().put("order:statuses", orderId.toString(), status);
            logger.info("Successfully cached order {} status {} in Redis with key {}", orderId, status, key);
        } catch (Exception e) {
            logger.error("Failed to cache order {} status in Redis: {}", orderId, e.getMessage());
        }
    }

    public void cacheOrderStatuses(Map<Integer, String> orderStatuses) {
        try {
            Map<String, String> keyValuePairs = new HashMap<>();
            orderStatuses.forEach((orderId, status) -> 
                keyValuePairs.put(KEY_PREFIX + orderId, status));
            
            redisTemplate.opsForValue().multiSet(keyValuePairs);
            
            // Set expiration for all keys
            keyValuePairs.keySet().forEach(key -> 
                redisTemplate.expire(key, CACHE_DURATION, TimeUnit.HOURS));
            
            logger.info("Successfully cached {} order statuses in Redis", orderStatuses.size());
        } catch (Exception e) {
            logger.error("Failed to cache order statuses in Redis: {}", e.getMessage());
        }
    }

    public String getOrderStatus(Integer orderId) {
        String key = KEY_PREFIX + orderId;
        try {
            String status = redisTemplate.opsForValue().get(key);
            if (status != null) {
                logger.info("Successfully retrieved order {} status {} from Redis with key {}", orderId, status, key);
            } else {
                logger.info("No cached status found in Redis for order {} with key {}", orderId, key);
            }
            return status;
        } catch (Exception e) {
            logger.error("Failed to get order {} status from Redis: {}", orderId, e.getMessage());
            return null;
        }
    }

    public Map<Integer, String> getOrderStatuses(List<Integer> orderIds) {
        try {
            List<String> keys = orderIds.stream()
                .map(id -> KEY_PREFIX + id)
                .toList();
            
            List<String> statuses = redisTemplate.opsForValue().multiGet(keys);
            
            Map<Integer, String> result = new HashMap<>();
            for (int i = 0; i < orderIds.size(); i++) {
                if (statuses.get(i) != null) {
                    result.put(orderIds.get(i), statuses.get(i));
                }
            }
            
            logger.info("Retrieved {} order statuses from Redis", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to get order statuses from Redis: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public void removeOrderStatus(Integer orderId) {
        String key = KEY_PREFIX + orderId;
        try {
            redisTemplate.delete(key);
            logger.info("Successfully removed order {} status from Redis with key {}", orderId, key);
        } catch (Exception e) {
            logger.error("Failed to remove order {} status from Redis: {}", orderId, e.getMessage());
        }
    }

    public Map<Integer, String> getAllOrderStatuses() {
        Map<Object, Object> rawStatuses = redisTemplate.opsForHash().entries("order:statuses");
        Map<Integer, String> statuses = new HashMap<>();
        rawStatuses.forEach((key, value) -> 
            statuses.put(Integer.parseInt(key.toString()), value.toString())
        );
        return statuses;
    }

    public void updateOrderStatus(Integer orderId, String status) {
        String key = KEY_PREFIX + orderId;
        redisTemplate.opsForValue().set(key, status, CACHE_DURATION, TimeUnit.HOURS);
        redisTemplate.opsForHash().put("order:statuses", orderId.toString(), status);
    }
} 