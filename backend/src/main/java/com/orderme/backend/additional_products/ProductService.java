package com.orderme.backend.additional_products;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<AdditionalProduct> getAllProducts() {
        return productRepository.findAll();
    }
}