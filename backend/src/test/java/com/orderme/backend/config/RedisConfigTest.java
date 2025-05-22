package com.orderme.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RedisConfigTest {

    @Test
    void redisTemplate_ShouldBeConfiguredCorrectly() {
        // Arrange
        RedisConfig config = new RedisConfig();
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        // Act
        RedisTemplate<String, String> template = config.redisTemplate(connectionFactory);

        // Assert
        assertNotNull(template);
        assertEquals(connectionFactory, template.getConnectionFactory());
        assertTrue(template.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(template.getValueSerializer() instanceof StringRedisSerializer);
    }
} 