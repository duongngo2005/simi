package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
