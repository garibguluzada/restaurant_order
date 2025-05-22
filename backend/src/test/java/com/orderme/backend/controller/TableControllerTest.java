package com.orderme.backend.controller;

import com.orderme.backend.table.RestaurantTable;
import com.orderme.backend.table.TableCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TableControllerTest {

    @Mock
    private TableCacheService tableCacheService;

    private TableController tableController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tableController = new TableController(tableCacheService);
    }

    @Test
    void getAllTables_ShouldReturnAllTables() {
        // Arrange
        List<RestaurantTable> expectedTables = createSampleTables();
        when(tableCacheService.getAllTables()).thenReturn(expectedTables);

        // Act
        List<RestaurantTable> actualTables = tableController.getAllTables();

        // Assert
        assertEquals(expectedTables, actualTables);
        verify(tableCacheService).getAllTables();
    }

    @Test
    void getTableById_ShouldReturnTable() {
        // Arrange
        String tableId = "table1";
        RestaurantTable expectedTable = createSampleTables().get(0);
        when(tableCacheService.getTableById(tableId)).thenReturn(java.util.Optional.of(expectedTable));

        // Act
        ResponseEntity<RestaurantTable> response = tableController.getTableById(tableId);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedTable, response.getBody());
        verify(tableCacheService).getTableById(tableId);
    }

    @Test
    void getTableById_ShouldReturnNotFound() {
        // Arrange
        String tableId = "nonexistent";
        when(tableCacheService.getTableById(tableId)).thenReturn(java.util.Optional.empty());

        // Act
        ResponseEntity<RestaurantTable> response = tableController.getTableById(tableId);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(tableCacheService).getTableById(tableId);
    }

    private List<RestaurantTable> createSampleTables() {
        RestaurantTable table1 = new RestaurantTable();
        table1.setTableid("table1");
        table1.setTablenum(1);

        RestaurantTable table2 = new RestaurantTable();
        table2.setTableid("table2");
        table2.setTablenum(2);

        return Arrays.asList(table1, table2);
    }
} 