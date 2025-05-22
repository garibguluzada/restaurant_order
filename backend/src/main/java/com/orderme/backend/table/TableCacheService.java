package com.orderme.backend.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TableCacheService {
    private static final Logger logger = LoggerFactory.getLogger(TableCacheService.class);
    private static final String KEY_PREFIX = "table:info:";
    private static final String ALL_TABLES_KEY = "table:all";
    private static final long CACHE_DURATION = 24; // 24 hours

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TableRepository tableRepository;

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setTableRepository(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<RestaurantTable> getAllTables() {
        try {
            // Try to get from Redis first
            String cachedTables = redisTemplate.opsForValue().get(ALL_TABLES_KEY);
            if (cachedTables != null) {
                logger.info("Retrieved all tables from Redis cache");
                return parseTablesFromJson(cachedTables);
            }

            // If not in Redis, get from database and cache
            logger.info("Tables not found in Redis, fetching from database");
            List<RestaurantTable> tables = tableRepository.findAll();
            cacheTables(tables);
            return tables;
        } catch (Exception e) {
            logger.error("Error getting tables from cache: {}", e.getMessage());
            return tableRepository.findAll();
        }
    }

    private void cacheTables(List<RestaurantTable> tables) {
        try {
            String tablesJson = convertTablesToJson(tables);
            redisTemplate.opsForValue().set(ALL_TABLES_KEY, tablesJson, CACHE_DURATION, TimeUnit.HOURS);
            logger.info("Cached {} tables in Redis", tables.size());
        } catch (Exception e) {
            logger.error("Failed to cache tables in Redis: {}", e.getMessage());
        }
    }

    private String convertTablesToJson(List<RestaurantTable> tables) {
        // Simple JSON format: [{"tableid":"id1","tablenum":1},{"tableid":"id2","tablenum":2}]
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tables.size(); i++) {
            RestaurantTable table = tables.get(i);
            json.append("{\"tableid\":\"").append(table.getTableid())
                .append("\",\"tablenum\":").append(table.getTablenum())
                .append("}");
            if (i < tables.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private List<RestaurantTable> parseTablesFromJson(String json) {
        List<RestaurantTable> tables = new ArrayList<>();
        try {
            // Remove the outer brackets
            json = json.substring(1, json.length() - 1);
            // Split by table objects
            String[] tableStrings = json.split("},");
            
            for (String tableStr : tableStrings) {
                // Clean up the string
                tableStr = tableStr.replace("{", "").replace("}", "");
                // Split into key-value pairs
                String[] pairs = tableStr.split(",");
                
                RestaurantTable table = new RestaurantTable();
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    String key = keyValue[0].replace("\"", "");
                    String value = keyValue[1].replace("\"", "");
                    
                    if ("tableid".equals(key)) {
                        table.setTableid(value);
                    } else if ("tablenum".equals(key)) {
                        table.setTablenum(Integer.parseInt(value));
                    }
                }
                tables.add(table);
            }
        } catch (Exception e) {
            logger.error("Error parsing tables from JSON: {}", e.getMessage());
        }
        return tables;
    }

    public Optional<RestaurantTable> getTableById(String tableId) {
        try {
            // Try to get from Redis first
            String cachedTable = redisTemplate.opsForValue().get(KEY_PREFIX + tableId);
            if (cachedTable != null) {
                logger.info("Retrieved table {} from Redis cache", tableId);
                return Optional.of(parseTableFromJson(cachedTable));
            }

            // If not in Redis, get from database and cache
            logger.info("Table {} not found in Redis, fetching from database", tableId);
            Optional<RestaurantTable> table = tableRepository.findById(tableId);
            table.ifPresent(this::cacheTable);
            return table;
        } catch (Exception e) {
            logger.error("Error getting table from cache: {}", e.getMessage());
            return tableRepository.findById(tableId);
        }
    }

    private void cacheTable(RestaurantTable table) {
        try {
            String tableJson = convertTableToJson(table);
            redisTemplate.opsForValue().set(KEY_PREFIX + table.getTableid(), tableJson, CACHE_DURATION, TimeUnit.HOURS);
            logger.info("Cached table {} in Redis", table.getTableid());
        } catch (Exception e) {
            logger.error("Failed to cache table in Redis: {}", e.getMessage());
        }
    }

    private String convertTableToJson(RestaurantTable table) {
        return String.format("{\"tableid\":\"%s\",\"tablenum\":%d}", 
            table.getTableid(), table.getTablenum());
    }

    private RestaurantTable parseTableFromJson(String json) {
        try {
            // Remove the outer brackets
            json = json.substring(1, json.length() - 1);
            // Split into key-value pairs
            String[] pairs = json.split(",");
            
            RestaurantTable table = new RestaurantTable();
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                String key = keyValue[0].replace("\"", "");
                String value = keyValue[1].replace("\"", "");
                
                if ("tableid".equals(key)) {
                    table.setTableid(value);
                } else if ("tablenum".equals(key)) {
                    table.setTablenum(Integer.parseInt(value));
                }
            }
            return table;
        } catch (Exception e) {
            logger.error("Error parsing table from JSON: {}", e.getMessage());
            return null;
        }
    }
} 