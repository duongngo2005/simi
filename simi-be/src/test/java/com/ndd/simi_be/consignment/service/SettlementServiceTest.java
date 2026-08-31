package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.cloudinary.CloudinaryService;
import com.ndd.simi_be.common.exception.ForbiddenException;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.Settlement;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.consignment.repository.ItemDispositionRepository;
import com.ndd.simi_be.consignment.repository.SettlementRepository;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ConsignmentRepository consignmentRepository;
    @Mock private CloudinaryService cloudinaryService;
    @Mock private SettlementRepository settlementRepository;
    @Mock private ItemDispositionRepository itemDispositionRepository;
    @InjectMocks private SettlementService settlementService;

    @Test
    void customerCannotReadAnotherConsignorsSettlement() {
        User owner = user(1L, Role.CUSTOMER);
        User otherCustomer = user(2L, Role.CUSTOMER);
        Consignment consignment = Consignment.builder().consignor(owner).build();
        Settlement settlement = Settlement.builder().consignment(consignment).build();

        when(settlementRepository.findByConsignmentId(9L)).thenReturn(Optional.of(settlement));

        assertThatThrownBy(() -> settlementService.getSettlement(9L, otherCustomer))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void settlementClosesImmediatelyWhenThereIsNoOutstandingDisposition() {
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.SETTLED)
                .build();
        consignment.setId(4L);
        when(itemDispositionRepository.existsByConsignmentItem_Consignment_IdAndItemDispositionStatusNot(
                4L, ItemDispositionStatus.COMPLETED
        )).thenReturn(false);

        settlementService.closeConsignmentIfReady(consignment);

        assertThat(consignment.getConsignmentStatus()).isEqualTo(ConsignmentStatus.CLOSED);
        assertThat(consignment.getClosedAt()).isNotNull();
    }

    @Test
    void settlementStaysOpenUntilEveryDispositionIsCompleted() {
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.SETTLED)
                .build();
        consignment.setId(4L);
        when(itemDispositionRepository.existsByConsignmentItem_Consignment_IdAndItemDispositionStatusNot(
                4L, ItemDispositionStatus.COMPLETED
        )).thenReturn(true);

        settlementService.closeConsignmentIfReady(consignment);

        assertThat(consignment.getConsignmentStatus()).isEqualTo(ConsignmentStatus.SETTLED);
        assertThat(consignment.getClosedAt()).isNull();
    }

    private User user(Long id, Role role) {
        User user = User.builder().role(role).email("user" + id + "@example.com").fullName("User").build();
        user.setId(id);
        return user;
    }
}
