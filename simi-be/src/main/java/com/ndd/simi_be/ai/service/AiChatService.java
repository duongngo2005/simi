package com.ndd.simi_be.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndd.simi_be.ai.dto.request.AiChatRequest;
import com.ndd.simi_be.ai.dto.request.AiSearchFilter;
import com.ndd.simi_be.ai.dto.response.AiChatResponse;
import com.ndd.simi_be.ai.dto.response.ProductSuggestionResponse;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.product.repository.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final ProductRepository productRepository;
    private final GeminiClient geminiClient;
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

        Specification<Product> spec = Specification.allOf(
                ProductSpecification.isAvailable(),
                ProductSpecification.hasGenders(filter.getGenders()),
                ProductSpecification.hasColors(filter.getColors()),
                ProductSpecification.hasSizes(filter.getSizes()),
                ProductSpecification.hasBrandNames(filter.getBrandNames()),

                ProductSpecification.hasAiKeywords(filter.getItemKeywords()),

                ProductSpecification.hasMaterials(filter.getMaterials()),

                ProductSpecification.hasMinPrice(filter.getMinPrice()),
                ProductSpecification.hasMaxPrice(filter.getMaxPrice()),
                ProductSpecification.hasProductCondition(filter.getCondition()),
                ProductSpecification.hasExcludedKeywords(filter.getExcludedKeywords())
        );

        List<Product> candidates = productRepository.findAll(
                spec,
                PageRequest.of(0, 40, Sort.by(Sort.Direction.DESC, "createdDate"))
        ).getContent();
        log.info("[AI Search] Lọc được {} ứng viên từ CSDL trong {}ms", candidates.size(), (System.currentTimeMillis() - startTime));

        if (candidates.isEmpty()) {
            return AiChatResponse.builder()
                    .reply("Dạ hiện tại Simi chưa tìm thấy món đồ nào khớp trọn vẹn các tiêu chí trên trong kho. Bạn có muốn nới rộng màu sắc, size hoặc tầm giá hơn không ạ? 🌿")
                    .suggestedProducts(Collections.emptyList())
                    .build();
        }

        Map<Long, Product> candidatesById = candidates.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(), (p1, p2) -> p1));

        String catalogText = buildProductCatalog(candidates);
        String phase2Json = geminiClient.rankAndReply(request.getMessage(), catalogText, request.getHistory());

        try {
            JsonNode resultNode = objectMapper.readTree(phase2Json);
            String reply = resultNode.path("reply").asText("Dạ Simi gợi ý cho bạn vài mẫu đồ xinh dưới đây nhé:");
            JsonNode idsNode = resultNode.path("productIds");

            List<ProductSuggestionResponse> suggestions = new ArrayList<>();
            Set<Long> addedIds = new HashSet<>();

            if (idsNode.isArray()) {
                for (JsonNode idNode : idsNode) {
                    Long prodId = idNode.asLong();
                    if (candidatesById.containsKey(prodId) && !addedIds.contains(prodId)) {
                        suggestions.add(mapToSuggestion(candidatesById.get(prodId)));
                        addedIds.add(prodId);
                        if (suggestions.size() == 3) break;
                    } else {
                        log.warn("[AI Warning] ID {} do Gemini sinh không thuộc candidates hợp lệ, đã loại bỏ!", prodId);
                    }
                }
            }

            return AiChatResponse.builder()
                    .reply(reply)
                    .suggestedProducts(suggestions)
                    .build();

        } catch (Exception e) {
            log.error("[AI Error] Lỗi parse kết quả Phase 2: {}", e.getMessage());
            return AiChatResponse.builder()
                    .reply("Dạ Simi Stylist đang bận một chút, bạn thử lại sau nhé! 🙏")
                    .suggestedProducts(Collections.emptyList())
                    .build();
        }
    }

    private String buildProductCatalog(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            String tagsStr = p.getTags().isEmpty() ? "" : p.getTags().stream().map(t -> "#" + t.getName()).collect(Collectors.joining(" "));
            String descCut = p.getDescription() != null ? p.getDescription().replaceAll("\n", " ").trim() : "";
            if (descCut.length() > 80) descCut = descCut.substring(0, 80) + "...";

            sb.append(String.format(
                    "- ID:%d | Tên: %s | Danh mục: %s | Brand: %s | Giá: %sđ | Size: %s | Màu: %s | Chất liệu: %s | Tag: %s | Mô tả: %s\n",
                    p.getId(), p.getName(),
                    p.getCategory() != null ? p.getCategory().getName() : "",
                    p.getBrand() != null ? p.getBrand().getName() : "Không",
                    p.getCurrentPrice() != null ? p.getCurrentPrice().toPlainString() : "0",
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