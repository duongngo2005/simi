package com.ndd.simi_be.ai.service;

import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiCategoryResolverTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AiCategoryResolver aiCategoryResolver;

    @Test
    void resolvesParentCategoryWithAllActiveDescendants() {
        Category root = category(6L, "Quần", "quan", true);
        Category kaki = category(8L, "Quần kaki", "quan-kaki", true);
        Category inactiveChild = category(9L, "Quần short", "quan-short", false);
        root.setChildren(List.of(kaki, inactiveChild));

        when(categoryRepository.findBySlug("quan")).thenReturn(Optional.of(root));

        AiCategoryResolver.ResolvedCategory result = aiCategoryResolver.resolve("quần");

        assertThat(result.slug()).isEqualTo("quan");
        assertThat(result.categoryIds()).containsExactly(6L, 8L);
    }

    @Test
    void resolvesLeafCategoryWithoutExpandingToItsSiblings() {
        Category kaki = category(8L, "Quần kaki", "quan-kaki", true);

        when(categoryRepository.findBySlug("quan-kaki")).thenReturn(Optional.of(kaki));

        AiCategoryResolver.ResolvedCategory result = aiCategoryResolver.resolve("Quần kaki");

        assertThat(result.slug()).isEqualTo("quan-kaki");
        assertThat(result.categoryIds()).containsExactly(8L);
    }

    @Test
    void fallsBackToMatchingActiveRootWhenThePhraseIsMoreSpecificThanTheTaxonomy() {
        Category root = category(1L, "Áo", "ao", true);

        when(categoryRepository.findBySlug("ao-so-mi")).thenReturn(Optional.empty());
        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(root));

        AiCategoryResolver.ResolvedCategory result = aiCategoryResolver.resolve("áo sơ mi");

        assertThat(result.slug()).isEqualTo("ao");
        assertThat(result.categoryIds()).containsExactly(1L);
    }

    @Test
    void leavesUnknownOrInactiveCategoriesUnresolved() {
        when(categoryRepository.findBySlug("ao-khoac-da")).thenReturn(Optional.empty());
        when(categoryRepository.findBySlug("quan-short")).thenReturn(Optional.of(category(9L, "Quần short", "quan-short", false)));
        when(categoryRepository.findByParentIsNull()).thenReturn(List.of());

        AiCategoryResolver.ResolvedCategory unknown = aiCategoryResolver.resolve("áo khoác da");
        AiCategoryResolver.ResolvedCategory inactive = aiCategoryResolver.resolve("quần short");

        assertThat(unknown.isResolved()).isFalse();
        assertThat(unknown.categoryIds()).isEmpty();
        assertThat(inactive.isResolved()).isFalse();
        assertThat(inactive.categoryIds()).isEmpty();
    }

    private Category category(Long id, String name, String slug, boolean active) {
        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .active(active)
                .build();
        category.setId(id);
        return category;
    }
}
