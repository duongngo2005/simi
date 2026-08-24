package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.ConfirmDonateItemDispositionsRequest;
import com.ndd.simi_be.consignment.dto.request.ItemDispositionFilterRequest;
import com.ndd.simi_be.consignment.dto.request.UpdateItemDispositionsTypeRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemDispositionService {
    private final ItemDispositionRepository itemDispositionRepository;
    private final ConsignmentRepository consignmentRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ItemDispositionResponse> getAllItemDispositionByConsignmentId(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (consignment.getConsignmentStatus() != ConsignmentStatus.SETTLED){
            throw new BadRequestException("Lô hàng chưa được kết toán");
        }

        return itemDispositionRepository.findByConsignmentItem_Consignment_Id(consignmentId)
                .stream()
                .map(ItemDispositionMapper::toItemDispositionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ItemDispositionResponse> searchItemDisposition(ItemDispositionFilterRequest filterRequest){
        Specification<ItemDisposition> specification = Specification.allOf(
                ItemDispositionSpecification.hasStatus(
                        ItemDispositionFilterRequest.parseItemDispositionStatus(filterRequest.getStatus())
                ),
                ItemDispositionSpecification.hasType(
                        ItemDispositionFilterRequest.parseItemDispositionType(filterRequest.getType())
                ),
                ItemDispositionSpecification.hasKeyword(filterRequest.getKeyword()),
                ItemDispositionSpecification.hasConsignmentId(filterRequest.getConsignmentId())
        );

        Sort sort = filterRequest.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filterRequest.getSortBy()).descending()
                : Sort.by(filterRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

         return itemDispositionRepository.findAll(specification, pageable)
                .map(ItemDispositionMapper::toItemDispositionResponse);
    }

    @Transactional(readOnly = true)
    public List<ItemDispositionResponse> getMyItemDispositionsByConsignmentId(
            Long consignmentId,
            User user
    ){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (!Objects.equals(consignment.getConsignor().getId(), user.getId())){
            throw new BadRequestException("Bạn không sở hữu lô ký gửi này");
        }

        if (consignment.getConsignmentStatus() != ConsignmentStatus.SETTLED){
            throw new BadRequestException("Lô hàng chưa được kết toán");
        }

        return itemDispositionRepository.findByConsignmentItem_Consignment_Id(consignmentId)
                .stream()
                .map(ItemDispositionMapper::toItemDispositionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemDispositionResponse getItemDisposition(Long itemDispositionId){
        ItemDisposition itemDisposition = itemDispositionRepository.findById(itemDispositionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết trả hàng này"));

        return ItemDispositionMapper.toItemDispositionResponse(itemDisposition);
    }

    @Transactional
    public void updateItemDispositionsType(
            User processedBy,
            UpdateItemDispositionsTypeRequest request
    ){
        if (request.getItemDispositionIds() == null || request.getItemDispositionIds().isEmpty()) {
            throw new BadRequestException("Danh sách xử lý không được để trống");
        }

        List<ItemDisposition> dispositions = itemDispositionRepository.findAllById(request.getItemDispositionIds());

        if (dispositions.size() != request.getItemDispositionIds().size()) {
            throw new ResourceNotFoundException("Một số chi tiết không tồn tại");
        }

        for (ItemDisposition item : dispositions){
            if (item.getItemDispositionStatus() == ItemDispositionStatus.COMPLETED) {
                throw new BadRequestException("Trạng thái món hàng đã hoàn thành");
            }

            if (item.getItemDispositionType() == ItemDispositionType.DONATE && request.getType() == ItemDispositionType.RETURN) {
                throw new BadRequestException("Món đồ đã quá hạn và chuyển sang trạng thái DONATE");
            }

            item.setItemDispositionStatus(ItemDispositionStatus.COMPLETED);
            item.setProcessedAt(LocalDateTime.now());
            item.setItemDispositionType(request.getType());
            item.setProcessedBy(processedBy);

            if (item.getConsignmentItem() != null){
                if (request.getType() == ItemDispositionType.RETURN){
                    item.getConsignmentItem().setConsignmentItemStatus(ConsignmentItemStatus.RETURNED);
                } else if (request.getType() == ItemDispositionType.DONATE){
                    item.getConsignmentItem().setConsignmentItemStatus(ConsignmentItemStatus.DONATED);
                }
            }
        }
    }
}
