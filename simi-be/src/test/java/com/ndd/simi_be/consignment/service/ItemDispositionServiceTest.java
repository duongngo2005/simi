package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.consignment.repository.ItemDispositionRepository;
import com.ndd.simi_be.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemDispositionServiceTest {

    @Mock private ItemDispositionRepository itemDispositionRepository;
    @Mock private ConsignmentRepository consignmentRepository;
    @Mock private SettlementService settlementService;
    @InjectMocks private ItemDispositionService itemDispositionService;

    @Test
    void confirmingReturnUpdatesItemAndChecksWhetherConsignmentCanClose() {
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.SETTLED)
                .build();
        ConsignmentItem item = ConsignmentItem.builder().consignment(consignment).build();
        ItemDisposition disposition = ItemDisposition.builder()
                .consignmentItem(item)
                .itemDispositionType(ItemDispositionType.RETURN)
                .itemDispositionStatus(ItemDispositionStatus.PENDING)
                .build();
        disposition.setId(8L);

        when(itemDispositionRepository.findAllById(List.of(8L))).thenReturn(List.of(disposition));

        itemDispositionService.confirmReturn(List.of(8L), User.builder().build());

        assertThat(disposition.getItemDispositionStatus()).isEqualTo(ItemDispositionStatus.COMPLETED);
        assertThat(item.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.RETURNED);
        verify(settlementService).closeConsignmentIfReady(consignment);
    }
}
