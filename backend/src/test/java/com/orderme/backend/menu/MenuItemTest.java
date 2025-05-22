package com.orderme.backend.menu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void testMenuItemCreation() {
        MenuItem menuItem = new MenuItem();
        menuItem.setMealid(1L);
        menuItem.setPictureofmeal("burger.jpg");
        menuItem.setMealname("Burger");
        menuItem.setPrice(9.99);
        menuItem.setMealcontent("Delicious beef burger");
        menuItem.setCategory("Main Course");
        menuItem.setAdditionalproducts("Cheese, Bacon");

        assertEquals(1L, menuItem.getMealid());
        assertEquals("burger.jpg", menuItem.getPictureofmeal());
        assertEquals("Burger", menuItem.getMealname());
        assertEquals(9.99, menuItem.getPrice());
        assertEquals("Delicious beef burger", menuItem.getMealcontent());
        assertEquals("Main Course", menuItem.getCategory());
        assertEquals("Cheese, Bacon", menuItem.getAdditionalproducts());
    }

    @Test
    void testMenuItemEquality() {
        MenuItem item1 = new MenuItem();
        item1.setMealid(1L);
        item1.setPictureofmeal("burger.jpg");
        item1.setMealname("Burger");
        item1.setPrice(9.99);
        item1.setMealcontent("Delicious beef burger");
        item1.setCategory("Main Course");
        item1.setAdditionalproducts("Cheese, Bacon");

        MenuItem item2 = new MenuItem();
        item2.setMealid(1L);
        item2.setPictureofmeal("burger.jpg");
        item2.setMealname("Burger");
        item2.setPrice(9.99);
        item2.setMealcontent("Delicious beef burger");
        item2.setCategory("Main Course");
        item2.setAdditionalproducts("Cheese, Bacon");

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
} 