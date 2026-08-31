package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.repository.PriceScheduleRepository;
import com.ndd.simi_be.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceMarkdownServiceTest {

    @Mock private PriceScheduleRepository priceScheduleRepository;
    @InjectMocks private PriceMarkdownService priceMarkdownService;

    @Test
    void delayedSchedulerAppliesTheLatestEligiblePriceInsteadOfRepositoryOrder() {
        Product product = Product.builder().currentPrice(BigDecimal.valueOf(100_000)).build();
        ConsignmentItem item = ConsignmentItem.builder()
                .product(product)
                .consignmentItemStatus(ConsignmentItemStatus.ACTIVE)
                .activatedAt(LocalDateTime.now().minusDays(31))
                .build();
        item.setId(3L);
        PriceSchedule day15 = PriceSchedule.builder()
                .consignmentItem(item)
                .effectiveAfterDays(15)
                .price(BigDecimal.valueOf(90_000))
                .build();
        PriceSchedule day30 = PriceSchedule.builder()
                .consignmentItem(item)
                .effectiveAfterDays(30)
                .price(BigDecimal.valueOf(80_000))
                .build();

        when(priceScheduleRepository.findByPriceScheduleStatusAndConsignmentItem_ConsignmentItemStatus(
                PriceScheduleStatus.PENDING, ConsignmentItemStatus.ACTIVE
        )).thenReturn(List.of(day30, day15));

        priceMarkdownService.applyPendingPriceSchedules();

        assertThat(product.getCurrentPrice()).isEqualByComparingTo("80000");
        assertThat(day15.getPriceScheduleStatus()).isEqualTo(PriceScheduleStatus.APPLIED);
        assertThat(day30.getPriceScheduleStatus()).isEqualTo(PriceScheduleStatus.APPLIED);
    }
}
