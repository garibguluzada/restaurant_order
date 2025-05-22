package com.orderme.backend.controller;

import com.orderme.backend.table.RestaurantTable;
import com.orderme.backend.table.TableCacheService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:5500")
public class TableController {
    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    private final TableCacheService tableCacheService;

    public TableController(TableCacheService tableCacheService) {
        this.tableCacheService = tableCacheService;
    }

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        logger.info("Fetching all tables");
        return tableCacheService.getAllTables();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable("id") String tableId) {
        logger.info("Fetching table with id: {}", tableId);
        return tableCacheService.getTableById(tableId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
} 