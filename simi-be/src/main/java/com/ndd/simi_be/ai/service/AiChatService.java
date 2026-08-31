package com.ndd.simi_be.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndd.simi_be.ai.dto.request.AiChatRequest;
import com.ndd.simi_be.ai.dto.request.AiSearchFilter;
import com.ndd.simi_be.ai.dto.request.AiSearchPreference;
import com.ndd.simi_be.ai.dto.response.AiChatResponse;
import com.ndd.simi_be.ai.dto.response.ProductSuggestionResponse;
import com.ndd.simi_be.common.utils.SlugUtils;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.product.repository.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final ProductRepository productRepository;
    private final GeminiClient geminiClient;
    private final AiCategoryResolver aiCategoryResolver;
    private final AiProductScorer aiProductScorer;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public AiChatResponse chat(AiChatRequest request) {
        long startTime = System.currentTimeMillis();

        AiSearchFilter filter = geminiClient.extractSearchFilter(request.getMessage(), request.getHistory());

        if (filter == null) {
            return AiChatResponse.builder()
                    .reply("Dạ Simi chưa hiểu rõ yêu cầu lắm, bạn có thể nêu rõ loại trang phục, màu sắc, size hoặc mức giá bạn muốn tìm nhé! 🌿")
                    .suggestedProducts(Collections.emptyList())
                    .build();
        }

        AiCategoryResolver.ResolvedCategory resolvedCategory = aiCategoryResolver.resolve(filter.getCategoryPhrase());
        List<String> effectiveSemanticTerms = effectiveSemanticTerms(filter, resolvedCategory);

        Specification<Product> spec = Specification.allOf(
                ProductSpecification.isAvailable(),
                ProductSpecification.hasCategoryIdIn(resolvedCategory.categoryIds()),
                ProductSpecification.hasGenders(filter.getGenders()),
                ProductSpecification.hasColors(filter.getColors()),
                ProductSpecification.hasSizes(filter.getSizes()),
                ProductSpecification.hasBrandNames(filter.getBrandNames()),
                ProductSpecification.hasMaterials(filter.getMaterials()),

                ProductSpecification.hasMinPrice(filter.getMinPrice()),
                ProductSpecification.hasMaxPrice(filter.getMaxPrice()),
                ProductSpecification.hasProductCondition(filter.getCondition()),
                ProductSpecification.hasExcludedKeywords(filter.getExcludedKeywords())
        );

        List<Product> hardValidCandidates = productRepository.findAll(spec);
        log.info(
                "[AI Search] categorySlug={}, hardValidCandidateCount={}, elapsedMs={}",
                resolvedCategory.slug(),
                hardValidCandidates.size(),
                System.currentTimeMillis() - startTime
        );

        if (hardValidCandidates.isEmpty()) {
            return noResult(filter, List.of(), false);
        }

        List<AiProductScorer.ScoredProduct> rankedCandidates = rankCandidates(
                hardValidCandidates,
                filter,
                effectiveSemanticTerms
        );
        if (rankedCandidates.isEmpty()) {
            return noResult(filter, effectiveSemanticTerms, true);
        }

        List<AiProductScorer.ScoredProduct> rerankBudget = rankedCandidates.stream()
                .limit(10)
                .toList();

        Map<Long, AiProductScorer.ScoredProduct> candidatesById = rerankBudget.stream()
                .collect(Collectors.toMap(candidate -> candidate.product().getId(), candidate -> candidate));

        String catalogText = buildProductCatalog(rerankBudget);
        String phase2Json = geminiClient.rankAndReply(request.getMessage(), catalogText, request.getHistory());
        if (phase2Json == null) {
            return fallbackToBackendRanking(rerankBudget);
        }

        try {
            JsonNode resultNode = objectMapper.readTree(phase2Json);
            JsonNode idsNode = resultNode.path("productIds");

            List<ProductSuggestionResponse> suggestions = new ArrayList<>();
            Set<Long> addedIds = new HashSet<>();

            if (idsNode.isArray()) {
                for (JsonNode idNode : idsNode) {
                    Long prodId = idNode.asLong();
                    if (candidatesById.containsKey(prodId) && !addedIds.contains(prodId)) {
                        suggestions.add(mapToSuggestion(candidatesById.get(prodId).product()));
                        addedIds.add(prodId);
                        if (suggestions.size() == 3) break;
                    } else {
                        log.warn("[AI Warning] ID {} do Gemini sinh không thuộc candidates hợp lệ, đã loại bỏ!", prodId);
                    }
                }
            }

            fillFromBackendRanking(suggestions, addedIds, rerankBudget);

            return AiChatResponse.builder()
                    .reply(defaultSuggestionReply(suggestions.size()))
                    .suggestedProducts(suggestions)
                    .build();

        } catch (Exception e) {
            log.error("[AI Error] Lỗi parse kết quả Phase 2: {}", e.getMessage());
            return fallbackToBackendRanking(rerankBudget);
        }
    }

    private List<AiProductScorer.ScoredProduct> rankCandidates(
            List<Product> hardValidCandidates,
            AiSearchFilter filter,
            List<String> semanticTerms
    ) {
        boolean hasSemanticTerms = aiProductScorer.hasSemanticTerms(semanticTerms);
        List<AiProductScorer.ScoredProduct> scoredCandidates = aiProductScorer
                .scoreAll(hardValidCandidates, semanticTerms)
                .stream()
                .filter(candidate -> !hasSemanticTerms || candidate.matchedTermCount() > 0)
                .toList();

        Comparator<AiProductScorer.ScoredProduct> ranking = Comparator
                .comparingInt(AiProductScorer.ScoredProduct::matchedTermCount)
                .reversed()
                .thenComparing(Comparator.comparingInt(AiProductScorer.ScoredProduct::semanticScore).reversed());

        if (hasPreference(filter, AiSearchPreference.GOOD_VALUE)) {
            ranking = ranking.thenComparing(
                    candidate -> candidate.product().getCurrentPrice(),
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        }

        ranking = ranking
                .thenComparing(
                        candidate -> candidate.product().getCreatedDate(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        candidate -> candidate.product().getId(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                );

        return scoredCandidates.stream().sorted(ranking).toList();
    }

    private List<String> effectiveSemanticTerms(
            AiSearchFilter filter,
            AiCategoryResolver.ResolvedCategory resolvedCategory
    ) {
        List<String> terms = new ArrayList<>();
        Set<String> consumedMaterialTerms = filter.getMaterials() == null
                ? Set.of()
                : filter.getMaterials().stream()
                .filter(Objects::nonNull)
                .map(SlugUtils::toSlug)
                .filter(term -> !term.isBlank())
                .collect(Collectors.toSet());

        if (filter.getSemanticTerms() != null) {
            filter.getSemanticTerms().stream()
                    .filter(Objects::nonNull)
                    .filter(term -> !term.isBlank())
                    .filter(term -> !consumedMaterialTerms.contains(SlugUtils.toSlug(term)))
                    .forEach(terms::add);
        }

        if (filter.getCategoryPhrase() != null && !filter.getCategoryPhrase().isBlank()) {
            String phraseSlug = SlugUtils.toSlug(filter.getCategoryPhrase());
            if (!phraseSlug.equals(resolvedCategory.slug())) {
                terms.add(filter.getCategoryPhrase());
            }
        }

        return List.copyOf(terms);
    }

    private boolean hasPreference(AiSearchFilter filter, AiSearchPreference preference) {
        return filter.getPreferences() != null && filter.getPreferences().contains(preference);
    }

    private void fillFromBackendRanking(
            List<ProductSuggestionResponse> suggestions,
            Set<Long> addedIds,
            List<AiProductScorer.ScoredProduct> rankedCandidates
    ) {
        for (AiProductScorer.ScoredProduct candidate : rankedCandidates) {
            if (suggestions.size() == 3) {
                return;
            }
            Long productId = candidate.product().getId();
            if (addedIds.add(productId)) {
                suggestions.add(mapToSuggestion(candidate.product()));
            }
        }
    }

    private AiChatResponse fallbackToBackendRanking(List<AiProductScorer.ScoredProduct> rankedCandidates) {
        List<ProductSuggestionResponse> suggestions = new ArrayList<>();
        fillFromBackendRanking(suggestions, new HashSet<>(), rankedCandidates);
        return AiChatResponse.builder()
                .reply(defaultSuggestionReply(suggestions.size()))
                .suggestedProducts(suggestions)
                .build();
    }

    private String defaultSuggestionReply(int suggestionCount) {
        return "Dạ Simi đã tìm được " + suggestionCount
                + " sản phẩm phù hợp trong kho. Bạn xem các gợi ý bên dưới nhé. 🌿";
    }

    private AiChatResponse noResult(
            AiSearchFilter filter,
            List<String> semanticTerms,
            boolean semanticEvidenceMissing
    ) {
        String category = filter.getCategoryPhrase() == null || filter.getCategoryPhrase().isBlank()
                ? "sản phẩm"
                : filter.getCategoryPhrase();
        List<String> criteria = new ArrayList<>();
        if (filter.getColors() != null && !filter.getColors().isEmpty()) {
            criteria.add("màu " + String.join(", ", filter.getColors()));
        }
        if (filter.getSizes() != null && !filter.getSizes().isEmpty()) {
            criteria.add("size " + String.join(", ", filter.getSizes()));
        }
        if (filter.getMaxPrice() != null) {
            criteria.add("giá không quá " + filter.getMaxPrice().toPlainString() + "đ");
        }
        if (filter.getMinPrice() != null) {
            criteria.add("giá từ " + filter.getMinPrice().toPlainString() + "đ");
        }

        String detail = criteria.isEmpty() ? category : category + " " + String.join(", ", criteria);
        String reply = semanticEvidenceMissing
                ? "Dạ Simi chưa có " + category + " với thông tin "
                + String.join(", ", semanticTerms) + " trong catalog hiện tại. "
                + "Bạn có thể thử mô tả một đặc điểm khác hoặc quay lại khi có sản phẩm phù hợp hơn nhé. 🌿"
                : "Dạ hiện Simi chưa có " + detail + " phù hợp trong kho. "
                + "Bạn có thể thử điều chỉnh một điều kiện tìm kiếm nếu muốn nhé. 🌿";

        return AiChatResponse.builder()
                .reply(reply)
                .suggestedProducts(Collections.emptyList())
                .build();
    }

    private String buildProductCatalog(List<AiProductScorer.ScoredProduct> candidates) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < candidates.size(); index++) {
            AiProductScorer.ScoredProduct candidate = candidates.get(index);
            Product p = candidate.product();
            String tagsStr = p.getTags().isEmpty() ? "" : p.getTags().stream().map(t -> "#" + t.getName()).collect(Collectors.joining(" "));
            String descCut = p.getDescription() != null ? p.getDescription().replaceAll("\n", " ").trim() : "";
            if (descCut.length() > 80) descCut = descCut.substring(0, 80) + "...";

            sb.append(String.format(
                    "- backendRank:%d | backendScore:%d | matchedTerms:%d | ID:%d | Tên: %s | Danh mục: %s | Brand: %s | Giá: %sđ | Tình trạng: %s | Giới tính: %s | Size: %s | Màu: %s | Chất liệu: %s | Tag: %s | Mô tả: %s\n",
                    index + 1,
                    candidate.semanticScore(),
                    candidate.matchedTermCount(),
                    p.getId(), p.getName(),
                    p.getCategory() != null ? p.getCategory().getName() : "",
                    p.getBrand() != null ? p.getBrand().getName() : "Không",
                    p.getCurrentPrice() != null ? p.getCurrentPrice().toPlainString() : "0",
                    p.getProductCondition() != null ? p.getProductCondition().name() : "",
                    p.getGender() != null ? p.getGender().name() : "",
                    p.getSize() != null ? p.getSize() : "",
                    p.getColor() != null ? p.getColor() : "",
                    p.getMaterial() != null ? p.getMaterial() : "",
                    tagsStr, descCut
            ));
        }
        return sb.toString();
    }

    private ProductSuggestionResponse mapToSuggestion(Product product) {
        String thumbnail = product.getProductImages().stream()
                .filter(ProductImage::isThumbnail)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return ProductSuggestionResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getCurrentPrice())
                .thumbnailUrl(thumbnail)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .size(product.getSize())
                .build();
    }
}
