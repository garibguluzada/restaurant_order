package com.orderme.backend.additional_products;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController(productService);
    }

    @Test
    void getProducts_ShouldReturnAllProducts() {
        // Arrange
        List<AdditionalProduct> expectedProducts = createSampleProducts();
        when(productService.getAllProducts()).thenReturn(expectedProducts);

        // Act
        List<AdditionalProduct> actualProducts = productController.getProducts();

        // Assert
        assertEquals(expectedProducts, actualProducts);
        verify(productService).getAllProducts();
    }

    private List<AdditionalProduct> createSampleProducts() {
        AdditionalProduct product1 = new AdditionalProduct();
        product1.setProdId(1);
        product1.setMealid(1);
        product1.setProducts("Extra Cheese");
        product1.setPrice(2.99);

        AdditionalProduct product2 = new AdditionalProduct();
        product2.setProdId(2);
        product2.setMealid(1);
        product2.setProducts("Bacon");
        product2.setPrice(3.99);

        return Arrays.asList(product1, product2);
    }
} 