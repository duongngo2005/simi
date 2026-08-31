package com.ndd.simi_be.ai.service;

import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.category.repository.CategoryRepository;
import com.ndd.simi_be.common.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AiCategoryResolver {
    private final CategoryRepository categoryRepository;

    public ResolvedCategory resolve(String categoryPhrase) {
        if (categoryPhrase == null || categoryPhrase.isBlank()) {
            return ResolvedCategory.unresolved();
        }

        String phraseSlug = SlugUtils.toSlug(categoryPhrase.trim());
        Optional<Category> category = categoryRepository.findBySlug(phraseSlug)
                .or(() -> findRootCategoryMatchingPhrase(phraseSlug));

        if (category.isEmpty() || !category.get().isActive()) {
            return ResolvedCategory.unresolved();
        }

        Set<Long> categoryIds = new LinkedHashSet<>();
        collectActiveDescendantIds(category.get(), categoryIds);
        return new ResolvedCategory(category.get().getSlug(), List.copyOf(categoryIds));
    }

    private Optional<Category> findRootCategoryMatchingPhrase(String phraseSlug) {
        return categoryRepository.findByParentIsNull().stream()
                .filter(Category::isActive)
                .filter(category -> phraseSlug.equals(category.getSlug())
                        || phraseSlug.startsWith(category.getSlug() + "-"))
                .max(Comparator.comparingInt(category -> category.getSlug().length()));
    }

    private void collectActiveDescendantIds(Category category, Set<Long> categoryIds) {
        if (!category.isActive()) {
            return;
        }

        categoryIds.add(category.getId());
        for (Category child : category.getChildren()) {
            collectActiveDescendantIds(child, categoryIds);
        }
    }

    public record ResolvedCategory(String slug, List<Long> categoryIds) {
        public static ResolvedCategory unresolved() {
            return new ResolvedCategory(null, List.of());
        }

        public boolean isResolved() {
            return slug != null;
        }
    }
}
