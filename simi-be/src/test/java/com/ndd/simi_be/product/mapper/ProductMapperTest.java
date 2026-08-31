package com.ndd.simi_be.product.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    @Test
    void productDetailUsesProductStatusFieldToMatchTheFrontendContract() throws Exception {
        Category category = Category.builder().name("Ao").slug("ao").build();
        category.setId(1L);
        Product product = Product.builder()
                .name("Ao")
                .category(category)
                .productStatus(ProductStatus.AVAILABLE)
                .build();

        String json = new ObjectMapper().writeValueAsString(ProductMapper.toProductDetailResponse(product));

        assertThat(json).contains("\"productStatus\":\"AVAILABLE\"");
        assertThat(json).doesNotContain("\"status\"");
    }
}
