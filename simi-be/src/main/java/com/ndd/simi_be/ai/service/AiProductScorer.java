package com.ndd.simi_be.ai.service;

import com.ndd.simi_be.common.utils.SlugUtils;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.tag.entity.Tag;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiProductScorer {
    private static final int TAG_SCORE = 12;
    private static final int CATEGORY_SCORE = 10;
    private static final int NAME_SCORE = 10;
    private static final int MATERIAL_SCORE = 6;
    private static final int DESCRIPTION_SCORE = 4;

    public List<ScoredProduct> scoreAll(Collection<Product> products, List<String> semanticTerms) {
        List<String> normalizedTerms = normalizeTerms(semanticTerms);
        return products.stream()
                .map(product -> score(product, normalizedTerms))
                .toList();
    }

    public boolean hasSemanticTerms(List<String> semanticTerms) {
        return !normalizeTerms(semanticTerms).isEmpty();
    }

    private ScoredProduct score(Product product, List<String> normalizedTerms) {
        int matchedTermCount = 0;
        int semanticScore = 0;

        for (String term : normalizedTerms) {
            int strongestEvidence = strongestEvidence(product, term);
            if (strongestEvidence > 0) {
                matchedTermCount++;
                semanticScore += strongestEvidence;
            }
        }

        return new ScoredProduct(product, matchedTermCount, semanticScore);
    }

    private int strongestEvidence(Product product, String term) {
        int score = 0;
        if (product.getTags().stream().map(Tag::getName).anyMatch(value -> containsTerm(value, term))) {
            score = Math.max(score, TAG_SCORE);
        }
        if (product.getCategory() != null && containsTerm(product.getCategory().getName(), term)) {
            score = Math.max(score, CATEGORY_SCORE);
        }
        if (containsTerm(product.getName(), term)) {
            score = Math.max(score, NAME_SCORE);
        }
        if (containsTerm(product.getMaterial(), term)) {
            score = Math.max(score, MATERIAL_SCORE);
        }
        if (containsTerm(product.getDescription(), term)) {
            score = Math.max(score, DESCRIPTION_SCORE);
        }
        return score;
    }

    private List<String> normalizeTerms(List<String> semanticTerms) {
        if (semanticTerms == null || semanticTerms.isEmpty()) {
            return List.of();
        }

        Set<String> terms = new LinkedHashSet<>();
        for (String term : semanticTerms) {
            if (term == null || term.isBlank()) {
                continue;
            }
            String normalized = SlugUtils.toSlug(term.trim());
            if (!normalized.isBlank()) {
                terms.add(normalized);
            }
        }
        return List.copyOf(terms);
    }

    private boolean containsTerm(String value, String normalizedTerm) {
        return value != null && SlugUtils.toSlug(value).contains(normalizedTerm);
    }

    public record ScoredProduct(Product product, int matchedTermCount, int semanticScore) {
    }
}
