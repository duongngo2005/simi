package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ConsignmentItemRepository consignmentItemRepository;

    @InjectMocks private OrderItemService orderItemService;

    @Test
    void customerReservationPreventsPosFromSellingTheSameProduct() {
        Product product = product();
        ConsignmentItem consignmentItem = activeConsignmentItem(product);
        Order onlineOrder = Order.builder().build();
        Order posOrder = Order.builder().build();

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderItemService.createOrderItem(request(), onlineOrder);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.RESERVED);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.RESERVED);
        assertThatThrownBy(() -> orderItemService.createPosOrderItem(request(), posOrder))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void posMarksProductAndConsignmentItemAsSoldTogether() {
        Product product = product();
        ConsignmentItem consignmentItem = activeConsignmentItem(product);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderItemService.createPosOrderItem(request(), Order.builder().build());

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SOLD);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.SOLD);
    }

    @Test
    void checkoutDoesNotReserveProductWhenConsignmentItemIsNotActive() {
        Product product = product();
        ConsignmentItem consignmentItem = activeConsignmentItem(product);
        consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.EXPIRED);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));

        assertThatThrownBy(() -> orderItemService.createOrderItem(request(), Order.builder().build()))
                .isInstanceOf(BadRequestException.class);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.EXPIRED);
        org.mockito.Mockito.verify(orderItemRepository, never()).save(any());
    }

    private Product product() {
        Product product = Product.builder()
                .name("Jacket")
                .currentPrice(BigDecimal.valueOf(100_000))
                .productStatus(ProductStatus.AVAILABLE)
                .build();
        product.setId(5L);
        return product;
    }

    private ConsignmentItem activeConsignmentItem(Product product) {
        return ConsignmentItem.builder()
                .product(product)
                .consignmentItemStatus(ConsignmentItemStatus.ACTIVE)
                .build();
    }

    private OrderItemRequest request() {
        return OrderItemRequest.builder().productId(5L).build();
    }
}
