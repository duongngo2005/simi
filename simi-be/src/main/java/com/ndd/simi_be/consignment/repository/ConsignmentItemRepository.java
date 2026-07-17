package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsignmentItemRepository extends JpaRepository<ConsignmentItem, Long> {
    Optional<ConsignmentItem> findByProduct(Product product);
}
