package com.orderme.backend.table;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantTableTest {

    @Test
    void testTableCreation() {
        RestaurantTable table = new RestaurantTable();
        table.setTableid("T1");
        table.setTablenum(1);

        assertEquals("T1", table.getTableid());
        assertEquals(1, table.getTablenum());
    }

    @Test
    void testTableEquality() {
        RestaurantTable table1 = new RestaurantTable();
        table1.setTableid("T1");
        table1.setTablenum(1);

        RestaurantTable table2 = new RestaurantTable();
        table2.setTableid("T1");
        table2.setTablenum(1);

        assertEquals(table1, table2);
        assertEquals(table1.hashCode(), table2.hashCode());
    }
} 