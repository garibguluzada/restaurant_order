package com.orderme.backend.additional_products;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<AdditionalProduct> expectedProducts = createSampleProducts();
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<AdditionalProduct> actualProducts = productService.getAllProducts();

        // Assert
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findAll();
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