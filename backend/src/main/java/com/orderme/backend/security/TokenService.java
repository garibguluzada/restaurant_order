package com.orderme.backend.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "jwt:";
    private static final long TOKEN_VALIDITY = 24; // 24 hours

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String username, String token) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, TOKEN_VALIDITY, TimeUnit.HOURS);
    }

    public String getToken(String username) {
        String key = TOKEN_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeToken(String username) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }

    public boolean validateToken(String username, String token) {
        String storedToken = getToken(username);
        return storedToken != null && storedToken.equals(token);
    }
} 