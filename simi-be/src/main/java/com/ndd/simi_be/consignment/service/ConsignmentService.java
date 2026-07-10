package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.cloudinary.CloudinaryService;
import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.request.ConsignmentItemRequest;
import com.ndd.simi_be.consignment.dto.request.ConsignmentRequest;
import com.ndd.simi_be.consignment.dto.request.PriceScheduleRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.mapper.ConsignmentItemMapper;
import com.ndd.simi_be.consignment.mapper.ConsignmentMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.dto.ProductRequest;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.service.ProductService;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsignmentService {
    private final ConsignmentRepository consignmentRepository;
    private final ConsignmentItemRepository consignmentItemRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final PriceScheduleService priceScheduleService;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ConsignmentResponse createConsignment(ConsignmentRequest request, Long receivedById){
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

        List<PriceSchedule> priceSchedules = new ArrayList<>();
        for (PriceScheduleRequest priceScheduleRequest : request.getPriceScheduleRequests()){
            PriceSchedule priceSchedule = priceScheduleService.createPriceSchedule(priceScheduleRequest, consignmentItem);
            priceSchedules.add(priceSchedule);
        }

        priceScheduleService.validatePriceSchedule(priceSchedules);

        for (PriceSchedule schedule : priceSchedules){
            schedule.setConsignmentItem(consignmentItem);
        }

        product.setCurrentPrice(
                priceSchedules.stream()
                        .filter(schedule -> schedule.getSequenceNo() == 1)
                        .findFirst()
                        .map(PriceSchedule::getPrice)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy mức giá thứ nhất"
                        ))
        );

        consignmentItem.setPriceSchedules(priceSchedules);

        return ConsignmentItemMapper.toConsignmentItemResponse(consignmentItem);
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
                    item.getProduct().setProductStatus(ProductStatus.AVAILABLE);
                });

        consignment.setConsignmentStatus(ConsignmentStatus.ACTIVE);
        consignment.setStartDate(LocalDateTime.now());
        consignment.setExpiryDate(LocalDateTime.now().plusDays(60));

        return ConsignmentMapper.toConsignmentResponse(consignment);
    }
}
