package com.orderme.backend.additional_products;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdditionalProductTest {

    @Test
    void testAdditionalProductCreation() {
        AdditionalProduct product = new AdditionalProduct();
        product.setProdId(1);
        product.setMealid(1);
        product.setProducts("Extra Cheese");
        product.setPrice(2.99);

        assertEquals(1, product.getProdId());
        assertEquals(1, product.getMealid());
        assertEquals("Extra Cheese", product.getProducts());
        assertEquals(2.99, product.getPrice());
    }

    @Test
    void testAdditionalProductEquality() {
        AdditionalProduct product1 = new AdditionalProduct();
        product1.setProdId(1);
        product1.setMealid(1);
        product1.setProducts("Extra Cheese");
        product1.setPrice(2.99);

        AdditionalProduct product2 = new AdditionalProduct();
        product2.setProdId(1);
        product2.setMealid(1);
        product2.setProducts("Extra Cheese");
        product2.setPrice(2.99);

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }
} 