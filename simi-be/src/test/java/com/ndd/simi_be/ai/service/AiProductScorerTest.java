package com.ndd.simi_be.ai.service;

import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.tag.entity.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AiProductScorerTest {
    private final AiProductScorer aiProductScorer = new AiProductScorer();

    @Test
    void countsOneTermOnceAndUsesItsStrongestEvidence() {
        Product product = product("Quần kaki form gọn", "Quần kaki", "form gọn", "Mẫu form gọn dễ mặc");

        AiProductScorer.ScoredProduct result = aiProductScorer
                .scoreAll(List.of(product), List.of("form gọn"))
                .getFirst();

        assertThat(result.matchedTermCount()).isEqualTo(1);
        assertThat(result.semanticScore()).isEqualTo(12);
    }

    @Test
    void prefersProductMatchingMoreIndependentSemanticTerms() {
        Product fullMatch = product("Quần basic", "Quần", "form gọn, công sở", null);
        Product partialMatch = product("Quần basic", "Quần", "form gọn", null);

        List<AiProductScorer.ScoredProduct> results = aiProductScorer.scoreAll(
                List.of(fullMatch, partialMatch),
                List.of("form gọn", "công sở")
        );

        assertThat(results.get(0).matchedTermCount()).isEqualTo(2);
        assertThat(results.get(1).matchedTermCount()).isEqualTo(1);
    }

    @Test
    void normalizesAndDeduplicatesSemanticTerms() {
        Product product = product("Quần basic", "Quần", "form gọn", null);

        AiProductScorer.ScoredProduct result = aiProductScorer
                .scoreAll(List.of(product), List.of("FORM GỌN", "form gon", "form gọn"))
                .getFirst();

        assertThat(result.matchedTermCount()).isEqualTo(1);
        assertThat(result.semanticScore()).isEqualTo(12);
    }

    @Test
    void givesCategoryEvidenceMoreWeightThanDescriptionEvidence() {
        Product categoryMatch = product("Quần basic", "Quần công sở", null, null);
        Product descriptionMatch = product("Quần basic", "Quần", null, "Phù hợp công sở");

        List<AiProductScorer.ScoredProduct> results = aiProductScorer.scoreAll(
                List.of(categoryMatch, descriptionMatch),
                List.of("công sở")
        );

        assertThat(results.get(0).semanticScore()).isEqualTo(10);
        assertThat(results.get(1).semanticScore()).isEqualTo(4);
    }

    @Test
    void returnsZeroEvidenceWhenNoSemanticTermMatches() {
        Product product = product("Quần basic", "Quần", "công sở", "Dễ mặc");

        AiProductScorer.ScoredProduct result = aiProductScorer
                .scoreAll(List.of(product), List.of("form gọn"))
                .getFirst();

        assertThat(result.matchedTermCount()).isZero();
        assertThat(result.semanticScore()).isZero();
    }

    @Test
    void keepsStructuredFieldsOutOfSemanticTermDetection() {
        Product product = product("Quần kaki", "Quần kaki", null, null);

        AiProductScorer.ScoredProduct result = aiProductScorer
                .scoreAll(List.of(product), List.of())
                .getFirst();

        assertThat(result.matchedTermCount()).isZero();
        assertThat(result.semanticScore()).isZero();
    }

    private Product product(String name, String categoryName, String tags, String description) {
        Category category = Category.builder()
                .name(categoryName)
                .slug(categoryName.toLowerCase().replace(" ", "-"))
                .build();
        List<Tag> productTags = tags == null
                ? List.of()
                : Arrays.stream(tags.split(", "))
                .map(tagName -> Tag.builder().name(tagName).slug(tagName.replace(" ", "-")).build())
                .toList();
        return Product.builder()
                .name(name)
                .category(category)
                .tags(productTags)
                .description(description)
                .build();
    }
}
