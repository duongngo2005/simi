package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsignmentServiceTest {

    @Mock private ConsignmentRepository consignmentRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ConsignmentService consignmentService;

    @Test
    void activationUsesTheDayZeroPriceRegardlessOfCollectionOrder() {
        Product product = Product.builder().productStatus(ProductStatus.DRAFT).build();
        PriceSchedule day30 = PriceSchedule.builder()
                .effectiveAfterDays(30)
                .price(BigDecimal.valueOf(80_000))
                .build();
        PriceSchedule dayZero = PriceSchedule.builder()
                .effectiveAfterDays(0)
                .price(BigDecimal.valueOf(100_000))
                .build();
        ConsignmentItem item = ConsignmentItem.builder()
                .product(product)
                .consignmentItemStatus(ConsignmentItemStatus.DRAFT)
                .priceSchedules(List.of(day30, dayZero))
                .build();
        Consignment consignment = consignmentWithItems(List.of(item));
        item.setConsignment(consignment);

        when(consignmentRepository.findById(1L)).thenReturn(Optional.of(consignment));

        consignmentService.activeConsignment(1L);

        assertThat(item.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.ACTIVE);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(product.getCurrentPrice()).isEqualByComparingTo("100000");
        assertThat(dayZero.getPriceScheduleStatus()).isEqualTo(PriceScheduleStatus.APPLIED);
        assertThat(day30.getPriceScheduleStatus()).isEqualTo(PriceScheduleStatus.PENDING);
        assertThat(consignment.getConsignmentStatus()).isEqualTo(ConsignmentStatus.ACTIVE);
    }

    @Test
    void activationRejectsAConsignmentWithoutDraftItems() {
        ConsignmentItem cancelledItem = ConsignmentItem.builder()
                .consignmentItemStatus(ConsignmentItemStatus.CANCELLED)
                .build();
        Consignment consignment = consignmentWithItems(List.of(cancelledItem));
        cancelledItem.setConsignment(consignment);

        when(consignmentRepository.findById(1L)).thenReturn(Optional.of(consignment));

        assertThatThrownBy(() -> consignmentService.activeConsignment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("DRAFT");
    }

    private Consignment consignmentWithItems(List<ConsignmentItem> items) {
        User consignor = user(10L, "Consignor");
        User receivedBy = user(11L, "Staff");
        Consignment consignment = Consignment.builder()
                .consignor(consignor)
                .receivedBy(receivedBy)
                .consignmentStatus(ConsignmentStatus.DRAFT)
                .consignmentItems(items)
                .build();
        consignment.setId(1L);
        return consignment;
    }

    private User user(Long id, String name) {
        User user = User.builder().fullName(name).email(name + "@example.com").build();
        user.setId(id);
        return user;
    }
}
