package com.ndd.simi_be.category.repository;

import com.ndd.simi_be.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Category> findByName(String name);
    List<Category> findByParentIsNull();

    Optional<Category> findBySlug(String slug);
}
