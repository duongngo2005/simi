package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsignmentExpiryServiceTest {

    @Mock private ConsignmentRepository consignmentRepository;
    @InjectMocks private ConsignmentExpiryService consignmentExpiryService;

    @Test
    void expiryDoesNotMoveConsignmentToSettlementWhileAnItemIsReserved() {
        Product activeProduct = Product.builder().productStatus(ProductStatus.AVAILABLE).build();
        Product reservedProduct = Product.builder().productStatus(ProductStatus.RESERVED).build();
        ConsignmentItem activeItem = ConsignmentItem.builder()
                .product(activeProduct)
                .consignmentItemStatus(ConsignmentItemStatus.ACTIVE)
                .build();
        ConsignmentItem reservedItem = ConsignmentItem.builder()
                .product(reservedProduct)
                .consignmentItemStatus(ConsignmentItemStatus.RESERVED)
                .build();
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.ACTIVE)
                .consignmentItems(List.of(activeItem, reservedItem))
                .build();

        when(consignmentRepository.findByConsignmentStatusAndExpiryDateBefore(
                org.mockito.ArgumentMatchers.eq(ConsignmentStatus.ACTIVE), any(LocalDateTime.class)
        )).thenReturn(List.of(consignment));

        consignmentExpiryService.processExpiredConsignments();

        assertThat(activeItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.EXPIRED);
        assertThat(activeProduct.getProductStatus()).isEqualTo(ProductStatus.EXPIRED);
        assertThat(reservedItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.RESERVED);
        assertThat(consignment.getConsignmentStatus()).isEqualTo(ConsignmentStatus.ACTIVE);
    }

    @Test
    void expiryMovesConsignmentToSettlementWhenNoItemIsReserved() {
        Product activeProduct = Product.builder().productStatus(ProductStatus.AVAILABLE).build();
        ConsignmentItem activeItem = ConsignmentItem.builder()
                .product(activeProduct)
                .consignmentItemStatus(ConsignmentItemStatus.ACTIVE)
                .build();
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.ACTIVE)
                .consignmentItems(List.of(activeItem))
                .build();

        when(consignmentRepository.findByConsignmentStatusAndExpiryDateBefore(
                org.mockito.ArgumentMatchers.eq(ConsignmentStatus.ACTIVE), any(LocalDateTime.class)
        )).thenReturn(List.of(consignment));

        consignmentExpiryService.processExpiredConsignments();

        assertThat(activeItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.EXPIRED);
        assertThat(consignment.getConsignmentStatus()).isEqualTo(ConsignmentStatus.PENDING_SETTLEMENT);
    }
}
