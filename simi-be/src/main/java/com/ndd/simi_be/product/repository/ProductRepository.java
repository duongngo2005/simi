package com.ndd.simi_be.product.repository;

import com.ndd.simi_be.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
