package com.orderme.backend.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {

    @Mock
    private MenuItemService menuItemService;

    private MenuController menuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        menuController = new MenuController(menuItemService);
    }

    @Test
    void getMenu_ShouldReturnAllMenuItems() {
        // Arrange
        List<MenuItem> expectedItems = createSampleMenuItems();
        when(menuItemService.getAllMenuItems()).thenReturn(expectedItems);

        // Act
        List<MenuItem> actualItems = menuController.getMenu();

        // Assert
        assertEquals(expectedItems, actualItems);
        verify(menuItemService).getAllMenuItems();
    }

    @Test
    void addMenuItem_ShouldSaveAndReturnItem() {
        // Arrange
        MenuItem menuItem = createSampleMenuItems().get(0);
        when(menuItemService.saveMenuItem(menuItem)).thenReturn(menuItem);

        // Act
        MenuItem savedItem = menuController.addMenuItem(menuItem);

        // Assert
        assertNotNull(savedItem);
        assertEquals(menuItem, savedItem);
        verify(menuItemService).saveMenuItem(menuItem);
    }

    private List<MenuItem> createSampleMenuItems() {
        MenuItem item1 = new MenuItem();
        item1.setMealid(1L);
        item1.setPictureofmeal("burger.jpg");
        item1.setMealname("Burger");
        item1.setPrice(9.99);
        item1.setMealcontent("Delicious beef burger");
        item1.setCategory("Main Course");
        item1.setAdditionalproducts("Cheese, Bacon");

        MenuItem item2 = new MenuItem();
        item2.setMealid(2L);
        item2.setPictureofmeal("pizza.jpg");
        item2.setMealname("Pizza");
        item2.setPrice(12.99);
        item2.setMealcontent("Margherita pizza");
        item2.setCategory("Main Course");
        item2.setAdditionalproducts("Extra cheese, Mushrooms");

        return Arrays.asList(item1, item2);
    }
} 