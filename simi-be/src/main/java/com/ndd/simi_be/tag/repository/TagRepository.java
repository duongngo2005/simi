package com.ndd.simi_be.tag.repository;

import com.ndd.simi_be.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByNameIgnoreCase(String name);
}
