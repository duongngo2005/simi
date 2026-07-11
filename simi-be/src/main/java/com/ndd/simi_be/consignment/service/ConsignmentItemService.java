package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.ConsignmentItemRequest;
import com.ndd.simi_be.consignment.dto.request.PriceScheduleRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.mapper.ConsignmentItemMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsignmentItemService {
    private final ConsignmentItemRepository consignmentItemRepository;
    private final PriceScheduleService priceScheduleService;
    private final ConsignmentRepository consignmentRepository;
    private final ProductService productService;

    @Transactional
    public ConsignmentItemResponse createConsignmentItem(
            ConsignmentItemRequest request,
            MultipartFile thumbnail,
            List<MultipartFile> imageProducts,
            Long consignmentId
    ){
        if (thumbnail == null || thumbnail.isEmpty()){
            throw new BadRequestException("Không có thumbnail");
        }

        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));


        Product product = productService.createProduct(
                request.getProductRequest(),
                thumbnail,
                imageProducts
        );


        ConsignmentItem consignmentItem = ConsignmentItem.builder()
                .consignment(consignment)
                .commissionRate(request.getCommissionRate())
                .product(product)
                .build();

        consignmentItem = consignmentItemRepository.save(consignmentItem);

        List<PriceSchedule> priceSchedules
                = priceScheduleService.createListPriceSchedule(request.getPriceScheduleRequests(), consignmentItem);

        PriceSchedule nowSchedule = priceSchedules.getFirst();
        product.setCurrentPrice(nowSchedule.getPrice());
        nowSchedule.setAppliedAt(LocalDateTime.now());
        nowSchedule.setPriceScheduleStatus(PriceScheduleStatus.APPLIED);

        consignmentItem.setPriceSchedules(priceSchedules);

        return ConsignmentItemMapper.toConsignmentItemResponse(consignmentItem);
    }
}
