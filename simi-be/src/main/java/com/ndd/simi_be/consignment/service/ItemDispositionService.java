package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ForbiddenException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.ItemDispositionFilterRequest;
import com.ndd.simi_be.consignment.dto.response.ItemDispositionResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import com.ndd.simi_be.consignment.mapper.ItemDispositionMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.consignment.repository.ItemDispositionRepository;
import com.ndd.simi_be.consignment.specification.ItemDispositionSpecification;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemDispositionService {
    private final ItemDispositionRepository itemDispositionRepository;
    private final ConsignmentRepository consignmentRepository;
    private final SettlementService settlementService;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ItemDispositionResponse> getAllItemDispositionByConsignmentId(
            Long consignmentId
    ) {
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (!isSettledOrClosed(consignment)) {
            throw new BadRequestException("Lô hàng chưa được kết toán");
        }

        return itemDispositionRepository.findByConsignmentItem_Consignment_Id(consignmentId)
                .stream()
                .map(ItemDispositionMapper::toItemDispositionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ItemDispositionResponse> searchItemDisposition(
            ItemDispositionFilterRequest filterRequest
    ) {
        Specification<ItemDisposition> specification = Specification.allOf(
                ItemDispositionSpecification.hasStatus(
                        ItemDispositionFilterRequest.parseItemDispositionStatus(
                                filterRequest.getStatus()
                        )
                ),
                ItemDispositionSpecification.hasType(
                        ItemDispositionFilterRequest.parseItemDispositionType(
                                filterRequest.getType()
                        )
                ),
                ItemDispositionSpecification.hasKeyword(filterRequest.getKeyword()),
                ItemDispositionSpecification.hasConsignmentId(
                        filterRequest.getConsignmentId()
                )
        );

        Sort sort = filterRequest.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filterRequest.getSortBy()).descending()
                : Sort.by(filterRequest.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                sort
        );

        return itemDispositionRepository.findAll(specification, pageable)
                .map(ItemDispositionMapper::toItemDispositionResponse);
    }

    @Transactional(readOnly = true)
    public List<ItemDispositionResponse> getMyItemDispositionsByConsignmentId(
            Long consignmentId,
            User user
    ) {
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (!Objects.equals(consignment.getConsignor().getId(), user.getId())) {
            throw new ForbiddenException("Bạn không sở hữu lô ký gửi này");
        }

        if (!isSettledOrClosed(consignment)) {
            throw new BadRequestException("Lô hàng chưa được kết toán");
        }

        return itemDispositionRepository.findByConsignmentItem_Consignment_Id(consignmentId)
                .stream()
                .map(ItemDispositionMapper::toItemDispositionResponse)
                .toList();
    }

    @Transactional
    public void moveExpiredReturnsToDonation() {
        List<ItemDisposition> expiredReturns =
                itemDispositionRepository
                        .findByItemDispositionTypeAndItemDispositionStatusAndPickupDeadlineBefore(
                                ItemDispositionType.RETURN,
                                ItemDispositionStatus.PENDING,
                                LocalDateTime.now()
                        );

        for (ItemDisposition item : expiredReturns) {
            item.setItemDispositionType(ItemDispositionType.DONATE);
            item.setItemDispositionStatus(ItemDispositionStatus.CONFIRM);
            item.setProcessedAt(null);
            item.setProcessedBy(null);
        }
    }

    @Transactional
    public void confirmReturn(
            List<Long> itemDispositionIds,
            User processedBy
    ) {
        List<ItemDisposition> items = getItems(itemDispositionIds);

        for (ItemDisposition item : items) {
            if (item.getItemDispositionType() != ItemDispositionType.RETURN
                    || item.getItemDispositionStatus() != ItemDispositionStatus.PENDING) {
                throw new BadRequestException("Món hàng không ở trạng thái chờ trả");
            }

            item.setItemDispositionStatus(ItemDispositionStatus.COMPLETED);
            item.setProcessedAt(LocalDateTime.now());
            item.setProcessedBy(processedBy);
            item.getConsignmentItem()
                    .setConsignmentItemStatus(ConsignmentItemStatus.RETURNED);
        }
        closeAffectedConsignments(items);
    }

    @Transactional
    public void confirmDonation(
            List<Long> itemDispositionIds,
            User processedBy
    ) {
        List<ItemDisposition> items = getItems(itemDispositionIds);

        for (ItemDisposition item : items) {
            if (item.getItemDispositionType() != ItemDispositionType.DONATE
                    || item.getItemDispositionStatus() != ItemDispositionStatus.CONFIRM) {
                throw new BadRequestException(
                        "Món hàng không ở trạng thái chờ xác nhận quyên góp"
                );
            }

            item.setItemDispositionStatus(ItemDispositionStatus.COMPLETED);
            item.setProcessedAt(LocalDateTime.now());
            item.setProcessedBy(processedBy);
            item.getConsignmentItem()
                    .setConsignmentItemStatus(ConsignmentItemStatus.DONATED);
        }
        closeAffectedConsignments(items);
    }

    private List<ItemDisposition> getItems(List<Long> itemDispositionIds) {
        if (itemDispositionIds == null || itemDispositionIds.isEmpty()) {
            throw new BadRequestException("Danh sách xử lý không được để trống");
        }

        List<ItemDisposition> items = itemDispositionRepository.findAllById(itemDispositionIds);

        if (items.size() != itemDispositionIds.size()) {
            throw new ResourceNotFoundException("Một số món hàng không tồn tại");
        }

        return items;
    }

    private boolean isSettledOrClosed(Consignment consignment) {
        return consignment.getConsignmentStatus() == ConsignmentStatus.SETTLED
                || consignment.getConsignmentStatus() == ConsignmentStatus.CLOSED;
    }

    private void closeAffectedConsignments(List<ItemDisposition> items) {
        items.stream()
                .map(item -> item.getConsignmentItem().getConsignment())
                .distinct()
                .forEach(settlementService::closeConsignmentIfReady);
    }
}
