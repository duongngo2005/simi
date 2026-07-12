package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.ConsignmentItemRequest;
import com.ndd.simi_be.consignment.dto.request.PriceScheduleRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentFullDetailResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.mapper.ConsignmentItemMapper;
import com.ndd.simi_be.consignment.mapper.ConsignmentMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
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

        if (consignment.getConsignmentStatus() == ConsignmentStatus.ACTIVE){
            throw new BadRequestException("Không thể thêm chi tiết lô hàng vào lô hàng đã Active");
        }

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

        consignmentItem.setPriceSchedules(priceSchedules);

        return ConsignmentItemMapper.toConsignmentItemResponse(consignmentItem);
    }

    @Transactional
    public ConsignmentItemResponse softDeleteConsignmentItem(
            Long consignmentId,
            Long consignmentItemId
    ){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        ConsignmentItem item = consignmentItemRepository.findById(consignmentItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng"));

        if (!item.getConsignment().getId().equals(consignmentId)) {
            throw new BadRequestException("Chi tiết lô hàng không thuộc về lô hàng");
        }

        item.setConsignmentItemStatus(ConsignmentItemStatus.CANCELLED);
        item.getProduct().setProductStatus(ProductStatus.CANCELLED);
        item.getPriceSchedules().stream()
            .filter(schedule -> schedule.getPriceScheduleStatus() == PriceScheduleStatus.PENDING)
            .forEach(schedule -> schedule.setPriceScheduleStatus(PriceScheduleStatus.CANCELLED));
        return ConsignmentItemMapper.toConsignmentItemResponse(item);
    }

    @Transactional
    public void hardDeleteConsignmentItem(
            Long consignmentId,
            Long consignmentItemId
    ){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        ConsignmentItem item = consignmentItemRepository.findById(consignmentItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng"));

        if (!item.getConsignment().getId().equals(consignmentId)) {
            throw new BadRequestException("Chi tiết lô hàng không thuộc về lô hàng");
        }

        if (item.getConsignmentItemStatus() != ConsignmentItemStatus.DRAFT){
            throw new BadRequestException("Chỉ có thể xóa chi tiết đơn hàng DRAFT");
        }

        priceScheduleService.hardDeletePriceSchedules(item.getPriceSchedules());

        Product product = item.getProduct();
        consignmentItemRepository.delete(item);
        productService.hardDeleteProduct(product);
    }

    @Transactional(readOnly = true)
    public ConsignmentFullDetailResponse getAllItemsByConsignmentId(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        return ConsignmentMapper.toConsignmentFullDetailResponse(consignment);
    }

    @Transactional(readOnly = true)
    public ConsignmentItemResponse getConsignmentItemById(
            Long consignmentItemId
    ){
        ConsignmentItem item = consignmentItemRepository.findById(consignmentItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng"));

        return ConsignmentItemMapper.toConsignmentItemResponse(item);
    }
}
