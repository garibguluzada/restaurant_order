package com.orderme.backend.additional_products;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
 
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<AdditionalProduct> getProducts() {
        return productService.getAllProducts();
    }
}