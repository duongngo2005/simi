package com.ndd.simi_be.product.repository;

import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Override
    @EntityGraph(attributePaths = {"category", "brand", "tags"})
    List<Product> findAll(Specification<Product> spec);

    long countByProductStatus(ProductStatus status);
    @Query("SELECT p.productStatus, COUNT(p) FROM Product p GROUP BY p.productStatus")
    List<Object[]> countByProductStatusGrouped();
}
