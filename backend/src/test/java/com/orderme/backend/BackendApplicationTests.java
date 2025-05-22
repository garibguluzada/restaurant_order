package com.orderme.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import com.orderme.backend.config.TestRedisConfig;

@DataRedisTest
@Import(TestRedisConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.data.redis.repositories.enabled=false",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
