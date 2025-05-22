package com.orderme.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderme.backend.BackendApplication;
import com.orderme.backend.dto.LoginRequest;
import com.orderme.backend.menu.MenuItem;
import com.orderme.backend.order.Order;
import com.orderme.backend.staff.Staff;
import com.orderme.backend.staff.StaffRepository;
import com.orderme.backend.table.RestaurantTable;
import com.orderme.backend.table.TableRepository;
import com.orderme.backend.user.User;
import com.orderme.backend.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BackendApplication.class, ApiIntegrationTest.TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class ApiIntegrationTest {

    @Configuration
    static class TestConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(redis.getHost());
            config.setPort(redis.getFirstMappedPort());
            return new LettuceConnectionFactory(config);
        }

        @Bean
        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());
            template.afterPropertiesSet();
            return template;
        }
    }

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    private String staffToken;
    private String clientToken;
    private String tableId;
    private Staff testStaff;
    private User testUser;

    @BeforeEach
    public void setup() throws Exception {
        // Wait for Redis to be ready
        Thread.sleep(1000);

        // Clean up existing data
        staffRepository.deleteAll();
        tableRepository.deleteAll();

        // Create and save a test staff member with unique username
        Staff staff = new Staff();
        staff.setUsername("teststaff_" + System.currentTimeMillis());
        staff.setPassword("testpass");
        staff.setRole("STAFF");
        staffRepository.save(staff);  // Save the staff member to the database

        // Create and save a test table with unique ID
        RestaurantTable table = new RestaurantTable();
        String tableId = "T" + System.currentTimeMillis();
        table.setTableid(tableId);
        table.setTablenum(1);
        tableRepository.save(table);  // Save the table to the database

        // Get staff token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(staff.getUsername());  // Use the unique username
        loginRequest.setPassword("testpass");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        staffToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        // Get client token
        result = mockMvc.perform(post("/api/auth/id-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + tableId + "\"}"))  // Use the unique table ID
                .andExpect(status().isOk())
                .andReturn();

        clientToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        // Create and save a test user
        testUser = new User();
        testUser.setUsername("testuser_" + System.currentTimeMillis());
        testUser.setPassword("testpass");
        testUser.setRole("USER");
        userRepository.save(testUser);

        // Store tableId for later use
        tableId = table.getTableid();
        testStaff = staff;
    }

    // @Test
    // public void testProductEndpoints() throws Exception {
    //     // Test get products with authentication
    //     mockMvc.perform(get("/api/products")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray());
    // }

    @Test
    public void testAuthenticationEndpoints() throws Exception {
        // Get the current staff username from the repository
        Staff staff = staffRepository.findAll().get(0);
        String currentUsername = staff.getUsername();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(currentUsername);  // Use the current username
        loginRequest.setPassword("testpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Test invalid staff login
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername(currentUsername);  // Use the current username
        invalidRequest.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());

        // Get the current table ID from the repository
        RestaurantTable table = tableRepository.findAll().get(0);
        String currentTableId = table.getTableid();

        // Test client login
        mockMvc.perform(post("/api/auth/id-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + currentTableId + "\"}"))  // Use the current table ID
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Test invalid client login
        mockMvc.perform(post("/api/auth/id-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"INVALID\"}"))
                .andExpect(status().isUnauthorized());
    }

    // @Test
    // public void testMenuEndpoints() throws Exception {
    //     // Test create menu item
    //     MenuItem menuItem = new MenuItem();
    //     menuItem.setMealname("Test Meal");
    //     menuItem.setMealcontent("Test Description");
    //     menuItem.setPrice(9.99);
    //     menuItem.setCategory("Test Category");

    //     MvcResult result = mockMvc.perform(post("/api/menu")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(menuItem)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.mealid").exists())
    //             .andExpect(jsonPath("$.mealname").value("Test Meal"))
    //             .andReturn();

    //     // Get created menu item ID
    //     Long mealId = objectMapper.readTree(result.getResponse().getContentAsString())
    //             .get("mealid").asLong();

    //     // Test get menu item
    //     mockMvc.perform(get("/api/menu/" + mealId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.mealid").value(mealId))
    //             .andExpect(jsonPath("$.mealname").value("Test Meal"));

    //     // Test get all menu items
    //     mockMvc.perform(get("/api/menu")
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray())
    //             .andExpect(jsonPath("$[0].mealid").exists());
    // }

    // @Test
    // public void testOrderEndpoints() throws Exception {
    //     // Create a menu item first
    //     MenuItem menuItem = new MenuItem();
    //     menuItem.setMealname("Order Test Meal");
    //     menuItem.setMealcontent("Test Description");
    //     menuItem.setPrice(12.99);
    //     menuItem.setCategory("Test Category");

    //     MvcResult menuResult = mockMvc.perform(post("/api/menu-items")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(menuItem)))
    //             .andExpect(status().isOk())
    //             .andReturn();

    //     Long mealId = objectMapper.readTree(menuResult.getResponse().getContentAsString())
    //             .get("mealid").asLong();

    //     // Test create order
    //     Order order = new Order();
    //     order.setMealname("Order Test Meal");
    //     order.setTableid(tableId);
    //     order.setStatus("PENDING");
    //     order.setTotalcost(new BigDecimal("12.99"));

    //     MvcResult result = mockMvc.perform(post("/api/orders")
    //             .header("Authorization", "Bearer " + clientToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(order)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.orderid").exists())
    //             .andExpect(jsonPath("$.status").value("PENDING"))
    //             .andReturn();

    //     // Get created order ID
    //     Integer orderId = objectMapper.readTree(result.getResponse().getContentAsString())
    //             .get("orderid").asInt();

    //     // Test get order
    //     mockMvc.perform(get("/api/orders/" + orderId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.orderid").value(orderId))
    //             .andExpect(jsonPath("$.status").value("PENDING"));

    //     // Test update order status
    //     mockMvc.perform(put("/api/orders/" + orderId + "/status")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content("{\"status\": \"IN_PREPARATION\"}"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value("IN_PREPARATION"));

    //     // Test get all orders
    //     mockMvc.perform(get("/api/orders")
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray())
    //             .andExpect(jsonPath("$[0].orderid").exists());
    // }

    // @Test
    // public void testUserManagementEndpoints() throws Exception {
    //     // Test create user
    //     User newUser = new User();
    //     newUser.setUsername("newuser_" + System.currentTimeMillis());
    //     newUser.setPassword("newpass");
    //     newUser.setRole("USER");

    //     MvcResult result = mockMvc.perform(post("/api/users")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(newUser)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id").exists())
    //             .andExpect(jsonPath("$.username").value(newUser.getUsername()))
    //             .andReturn();

    //     // Get created user ID
    //     Long userId = objectMapper.readTree(result.getResponse().getContentAsString())
    //             .get("id").asLong();

    //     // Test get user
    //     mockMvc.perform(get("/api/users/" + userId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id").value(userId))
    //             .andExpect(jsonPath("$.username").value(newUser.getUsername()));

    //     // Test update user
    //     newUser.setUsername("updated_" + newUser.getUsername());
    //     mockMvc.perform(put("/api/users/" + userId)
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(newUser)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.username").value(newUser.getUsername()));

    //     // Test get all users
    //     mockMvc.perform(get("/api/users")
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray())
    //             .andExpect(jsonPath("$[0].id").exists());

    //     // Test delete user
    //     mockMvc.perform(delete("/api/users/" + userId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk());

    //     // Verify user is deleted
    //     mockMvc.perform(get("/api/users/" + userId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isNotFound());
    // }

    // @Test
    // public void testTableManagementEndpoints() throws Exception {
    //     // Test create table
    //     RestaurantTable newTable = new RestaurantTable();
    //     String newTableId = "T" + System.currentTimeMillis();
    //     newTable.setTableid(newTableId);
    //     newTable.setTablenum(2);

    //     MvcResult result = mockMvc.perform(put("/api/tables")
    //             .header("Authorization", "Bearer " + staffToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(newTable)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.tableid").value(newTableId))
    //             .andReturn();

    //     // Test get table
    //     mockMvc.perform(get("/api/tables/" + newTableId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.tableid").value(newTableId))
    //             .andExpect(jsonPath("$.tablenum").value(2));

    //     // Test get all tables
    //     mockMvc.perform(get("/api/tables")
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray())
    //             .andExpect(jsonPath("$[0].tableid").exists());

    //     // Test delete table
    //     mockMvc.perform(delete("/api/tables/" + newTableId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isOk());

    //     // Verify table is deleted
    //     mockMvc.perform(get("/api/tables/" + newTableId)
    //             .header("Authorization", "Bearer " + staffToken))
    //             .andExpect(status().isNotFound());
    // }
} 