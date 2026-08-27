package com.ndd.simi_be.product.repository;

import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    long countByProductStatus(ProductStatus status);
    @Query("SELECT p.productStatus, COUNT(p) FROM Product p GROUP BY p.productStatus")
    List<Object[]> countByProductStatusGrouped();
}
