package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderItem createOrderItem(OrderItemRequest request, Order order){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        if (product.getProductStatus() != ProductStatus.AVAILABLE){
            throw new BadRequestException("Sản phẩm hiện tại không khả dụng");
        }

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .order(order)
                .unitPrice(product.getCurrentPrice())
                .build();

        product.setProductStatus(ProductStatus.RESERVED);

        return orderItemRepository.save(orderItem);
    }
}
