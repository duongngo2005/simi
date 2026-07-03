package com.ndd.simi_be.category.repository;

import com.ndd.simi_be.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
