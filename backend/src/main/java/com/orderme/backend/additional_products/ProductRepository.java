package com.orderme.backend.additional_products;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<AdditionalProduct, Integer> {
}
