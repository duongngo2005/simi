package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.CreateConsignmentRequest;
import com.ndd.simi_be.consignment.dto.request.UpdateConsignmentRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.mapper.ConsignmentMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsignmentService {
    private final ConsignmentRepository consignmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ConsignmentResponse createConsignment(CreateConsignmentRequest request, Long receivedById){
        User receivedBy = userRepository.findById(receivedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        User consignor = userRepository.findById(request.getConsignorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng"));

        Consignment consignment = Consignment.builder()
                .receivedBy(receivedBy)
                .consignor(consignor)
                .note(request.getNote())
                .build();

        consignment = consignmentRepository.save(consignment);

        return ConsignmentMapper.toConsignmentResponse(consignment);
    }

    @Transactional(readOnly = true)
    public List<ConsignmentResponse> getAllConsignments(){
        return consignmentRepository.findAll().stream()
                .map(ConsignmentMapper::toConsignmentResponse).toList();
    }

    @Transactional
    public ConsignmentResponse activeConsignment(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (consignment.getConsignmentItems().isEmpty()){
            throw new BadRequestException("Không thể kích hoạt lô hàng rỗng");
        }

        if (consignment.getConsignmentStatus() != ConsignmentStatus.DRAFT){
            throw new BadRequestException("Lô hàng đã được active");
        }

        consignment.getConsignmentItems().stream()
                .filter(item -> item.getConsignmentItemStatus() ==  ConsignmentItemStatus.DRAFT)
                .forEach(item -> {
                    item.setConsignmentItemStatus(ConsignmentItemStatus.ACTIVE);
                    item.setActivatedAt(LocalDateTime.now());
                    PriceSchedule nowSchedule = item.getPriceSchedules().getFirst();
                    item.getProduct().setProductStatus(ProductStatus.AVAILABLE);
                    item.getProduct().setCurrentPrice(nowSchedule.getPrice());
                    nowSchedule.setAppliedAt(LocalDateTime.now());
                    nowSchedule.setPriceScheduleStatus(PriceScheduleStatus.APPLIED);
                });

        consignment.setConsignmentStatus(ConsignmentStatus.ACTIVE);
        consignment.setStartDate(LocalDateTime.now());
        consignment.setExpiryDate(LocalDateTime.now().plusDays(60));

        return ConsignmentMapper.toConsignmentResponse(consignment);
    }

    @Transactional
    public ConsignmentResponse updateConsignment(UpdateConsignmentRequest request, Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        consignment.setNote(request.getNote());
        consignmentRepository.save(consignment);
        return ConsignmentMapper.toConsignmentResponse(consignment);
    }

    @Transactional
    public void deleteConsignment(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (!consignment.getConsignmentItems().isEmpty()){
            throw new ConflictException("Không thể xóa lô hàng đang có chi tiết lô hàng");
        }

        consignmentRepository.delete(consignment);
    }

    @Transactional(readOnly = true)
    public ConsignmentResponse getConsignmentById(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        return ConsignmentMapper.toConsignmentResponse(consignment);
    }
}
