package com.orderme.backend.table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TableCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private TableRepository tableRepository;

    private TableCacheService tableCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tableCacheService = new TableCacheService();
        tableCacheService.setRedisTemplate(redisTemplate);
        tableCacheService.setTableRepository(tableRepository);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getAllTables_ShouldReturnTablesFromCache() {
        // Arrange
        List<RestaurantTable> expectedTables = createSampleTables();
        String tablesJson = "[{\"tableid\":\"T1\",\"tablenum\":1},{\"tableid\":\"T2\",\"tablenum\":2}]";
        when(valueOperations.get("table:all")).thenReturn(tablesJson);

        // Act
        List<RestaurantTable> actualTables = tableCacheService.getAllTables();

        // Assert
        assertNotNull(actualTables);
        assertEquals(2, actualTables.size());
        assertEquals(expectedTables.get(0), actualTables.get(0));
        assertEquals(expectedTables.get(1), actualTables.get(1));
        verify(valueOperations).get("table:all");
    }

    @Test
    void getAllTables_ShouldReturnTablesFromDatabase_WhenCacheMiss() {
        // Arrange
        List<RestaurantTable> expectedTables = createSampleTables();
        when(valueOperations.get("table:all")).thenReturn(null);
        when(tableRepository.findAll()).thenReturn(expectedTables);

        // Act
        List<RestaurantTable> actualTables = tableCacheService.getAllTables();

        // Assert
        assertNotNull(actualTables);
        assertEquals(expectedTables, actualTables);
        verify(tableRepository).findAll();
        verify(valueOperations).set(eq("table:all"), anyString(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void getTableById_ShouldReturnTableFromCache() {
        // Arrange
        RestaurantTable expectedTable = createSampleTables().get(0);
        String tableJson = "{\"tableid\":\"T1\",\"tablenum\":1}";
        when(valueOperations.get("table:info:T1")).thenReturn(tableJson);

        // Act
        Optional<RestaurantTable> actualTable = tableCacheService.getTableById("T1");

        // Assert
        assertTrue(actualTable.isPresent());
        assertEquals(expectedTable, actualTable.get());
        verify(valueOperations).get("table:info:T1");
    }

    @Test
    void getTableById_ShouldReturnTableFromDatabase_WhenCacheMiss() {
        // Arrange
        RestaurantTable expectedTable = createSampleTables().get(0);
        when(valueOperations.get("table:info:T1")).thenReturn(null);
        when(tableRepository.findById("T1")).thenReturn(Optional.of(expectedTable));

        // Act
        Optional<RestaurantTable> actualTable = tableCacheService.getTableById("T1");

        // Assert
        assertTrue(actualTable.isPresent());
        assertEquals(expectedTable, actualTable.get());
        verify(tableRepository).findById("T1");
        verify(valueOperations).set(eq("table:info:T1"), anyString(), eq(24L), eq(TimeUnit.HOURS));
    }

    private List<RestaurantTable> createSampleTables() {
        RestaurantTable table1 = new RestaurantTable();
        table1.setTableid("T1");
        table1.setTablenum(1);

        RestaurantTable table2 = new RestaurantTable();
        table2.setTableid("T2");
        table2.setTablenum(2);

        return Arrays.asList(table1, table2);
    }
} 