package com.ndd.simi_be.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndd.simi_be.ai.dto.request.AiChatRequest;
import com.ndd.simi_be.ai.dto.request.AiSearchFilter;
import com.ndd.simi_be.ai.dto.request.AiSearchPreference;
import com.ndd.simi_be.ai.dto.response.AiChatResponse;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.tag.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.jpa.domain.Specification;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private GeminiClient geminiClient;
    @Mock
    private AiCategoryResolver aiCategoryResolver;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        aiChatService = new AiChatService(
                productRepository,
                geminiClient,
                aiCategoryResolver,
                new AiProductScorer(),
                new ObjectMapper()
        );
        lenient().when(aiCategoryResolver.resolve(any())).thenReturn(AiCategoryResolver.ResolvedCategory.unresolved());
    }

    @Test
    void goodValueDoesNotOverrideAProductMatchingMoreSemanticTerms() {
        Product fullMatch = product(1L, "Quần kaki công sở", 500_000, "form gọn, công sở", 1);
        Product partialMatch = product(2L, "Quần kaki form gọn", 200_000, "form gọn", 2);
        AiSearchFilter filter = AiSearchFilter.builder()
                .categoryPhrase("quần kaki")
                .semanticTerms(List.of("form gọn", "công sở"))
                .preferences(List.of(AiSearchPreference.GOOD_VALUE))
                .build();

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(partialMatch, fullMatch));
        when(geminiClient.rankAndReply(any(), any(), any())).thenReturn("{\"reply\":\"\",\"productIds\":[]}");

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId())
                .containsExactly(1L, 2L);
    }

    @Test
    void returnsNoResultWithoutCallingRerankWhenSemanticEvidenceIsMissing() {
        Product product = product(1L, "Quần kaki", 200_000, "công sở", 1);
        AiSearchFilter filter = AiSearchFilter.builder()
                .categoryPhrase("quần")
                .semanticTerms(List.of("form gọn"))
                .build();

        when(aiCategoryResolver.resolve("quần"))
                .thenReturn(new AiCategoryResolver.ResolvedCategory("quan", List.of(1L)));
        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(product));

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).isEmpty();
        assertThat(response.getReply()).contains("form gọn");
        verify(geminiClient, never()).rankAndReply(any(), any(), any());
    }

    @Test
    void keepsValidGeminiIdsAndFillsMissingSuggestionsFromBackendRanking() {
        Product first = product(1L, "Quần kaki A", 300_000, null, 1);
        Product second = product(2L, "Quần kaki B", 200_000, null, 2);
        Product third = product(3L, "Quần kaki C", 100_000, null, 3);
        AiSearchFilter filter = AiSearchFilter.builder().categoryPhrase("quần kaki").build();

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(first, second, third));
        when(geminiClient.rankAndReply(any(), any(), any()))
                .thenReturn("{\"reply\":\"Mẫu phù hợp\",\"productIds\":[2,999]}");

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId())
                .containsExactly(2L, 3L, 1L);
        assertThat(response.getReply())
                .contains("3 sản phẩm")
                .doesNotContain("Mẫu phù hợp");
    }

    @Test
    void fallsBackToBackendRankingWhenGeminiRerankFails() {
        Product first = product(1L, "Quần kaki A", 300_000, null, 1);
        Product second = product(2L, "Quần kaki B", 200_000, null, 2);
        AiSearchFilter filter = AiSearchFilter.builder().categoryPhrase("quần kaki").build();

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(first, second));
        when(geminiClient.rankAndReply(any(), any(), any())).thenReturn(null);

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId())
                .containsExactly(2L, 1L);
        assertThat(response.getReply()).contains("gợi ý");
    }

    @Test
    void fallsBackToBackendRankingWhenGeminiReturnsMalformedJson() {
        Product first = product(1L, "Quần kaki A", 300_000, null, 1);
        Product second = product(2L, "Quần kaki B", 200_000, null, 2);
        AiSearchFilter filter = AiSearchFilter.builder().build();

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(first, second));
        when(geminiClient.rankAndReply(any(), any(), any())).thenReturn("không phải JSON");

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId())
                .containsExactly(2L, 1L);
        assertThat(response.getReply()).contains("gợi ý");
    }

    @Test
    void usesDescendingIdAsTheFinalDeterministicTieBreaker() {
        Product lowerId = product(1L, "Quần kaki A", 200_000, null, 1);
        Product higherId = product(2L, "Quần kaki B", 200_000, null, 1);
        LocalDateTime sameCreatedDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        lowerId.setCreatedDate(sameCreatedDate);
        higherId.setCreatedDate(sameCreatedDate);
        AiSearchFilter filter = AiSearchFilter.builder().build();

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(lowerId, higherId));
        when(geminiClient.rankAndReply(any(), any(), any()))
                .thenReturn("{\"reply\":\"\",\"productIds\":[]}");

        AiChatResponse response = aiChatService.chat(request());

        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId())
                .containsExactly(2L, 1L);
    }

    @Test
    void doesNotScoreAMaterialAgainAfterItWasUsedAsAHardFilter() {
        Product product = product(1L, "Quần kaki", 200_000, null, 1);
        product.setMaterial("Polyester");
        AiSearchFilter filter = AiSearchFilter.builder()
                .materials(List.of("polyester"))
                .semanticTerms(List.of("polyester"))
                .build();
        ArgumentCaptor<String> catalogCaptor = ArgumentCaptor.forClass(String.class);

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(product));
        when(geminiClient.rankAndReply(any(), catalogCaptor.capture(), any()))
                .thenReturn("{\"reply\":\"\",\"productIds\":[]}");

        aiChatService.chat(request());

        assertThat(catalogCaptor.getValue()).contains("backendScore:0 | matchedTerms:0");
    }

    @Test
    void sendsOnlyTheTopTenCandidatesToGemini() {
        List<Product> products = java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(id -> product((long) id, "Quần " + id, 100_000 + id, null, id))
                .toList();
        AiSearchFilter filter = AiSearchFilter.builder().categoryPhrase("quần").build();
        ArgumentCaptor<String> catalogCaptor = ArgumentCaptor.forClass(String.class);

        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(products);
        when(geminiClient.rankAndReply(any(), catalogCaptor.capture(), any()))
                .thenReturn("{\"reply\":\"\",\"productIds\":[]}");

        aiChatService.chat(request());

        assertThat(catalogCaptor.getValue()).contains("backendRank:10").doesNotContain("backendRank:11");
    }

    @Test
    void keepsAnUnresolvedCategoryDetailAsSemanticEvidenceAfterBroadResolution() {
        Product shirt = product(1L, "Áo sơ mi linen", 300_000, null, 1);
        shirt.setCategory(Category.builder().name("Áo").slug("ao").build());
        Product tee = product(2L, "Áo thun basic", 200_000, null, 2);
        tee.setCategory(Category.builder().name("Áo").slug("ao").build());
        AiSearchFilter filter = AiSearchFilter.builder().categoryPhrase("áo sơ mi").build();
        ArgumentCaptor<String> catalogCaptor = ArgumentCaptor.forClass(String.class);

        when(aiCategoryResolver.resolve("áo sơ mi"))
                .thenReturn(new AiCategoryResolver.ResolvedCategory("ao", List.of(1L)));
        when(geminiClient.extractSearchFilter(any(), any())).thenReturn(filter);
        when(productRepository.findAll(anySpecification())).thenReturn(List.of(shirt, tee));
        when(geminiClient.rankAndReply(any(), catalogCaptor.capture(), any()))
                .thenReturn("{\"reply\":\"\",\"productIds\":[]}");

        AiChatResponse response = aiChatService.chat(request());

        assertThat(catalogCaptor.getValue()).contains("ID:1").doesNotContain("ID:2");
        assertThat(response.getSuggestedProducts()).extracting(product -> product.getId()).containsExactly(1L);
    }

    private AiChatRequest request() {
        return AiChatRequest.builder().message("tìm sản phẩm").build();
    }

    @SuppressWarnings("unchecked")
    private Specification<Product> anySpecification() {
        return any(Specification.class);
    }

    private Product product(Long id, String name, int price, String tagName, int createdOrder) {
        Category category = Category.builder().name("Quần kaki").slug("quan-kaki").build();
        Product product = Product.builder()
                .name(name)
                .category(category)
                .currentPrice(BigDecimal.valueOf(price))
                .tags(tagName == null ? List.of() : List.of(Tag.builder().name(tagName).slug(tagName.replace(" ", "-")).build()))
                .build();
        product.setId(id);
        product.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0).plusDays(createdOrder));
        return product;
    }
}
