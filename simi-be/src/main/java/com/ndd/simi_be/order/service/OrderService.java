package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.location.entity.Province;
import com.ndd.simi_be.location.entity.Ward;
import com.ndd.simi_be.location.repository.ProvinceRepository;
import com.ndd.simi_be.location.repository.WardRepository;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.mapper.OrderMapper;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final OrderItemService orderItemService;

    @Transactional
    public OrderDetailResponse createOrder(OrderRequest request, User customer){

        Province province = provinceRepository.findById(request.getProvince())
                .orElseThrow(() -> new ResourceNotFoundException("Tỉnh/thành phố không hợp lệ"));

        Ward ward = wardRepository.findById(request.getWard())
                .orElseThrow(() -> new ResourceNotFoundException("Xã/phường không hợp lệ"));

        if (!ward.getProvinceCode().equals(province.getCode())){
            throw new BadRequestException("Xã/phường không thuộc tỉnh");
        }

        Order order = Order.builder()
                .customer(customer)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .ward(ward.getFullName())
                .province(province.getFullName())
                .addressDetail(request.getAddressDetail())
                .discount(request.getDiscount())
                .orderChannel(OrderChannel.ONLINE)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getOrderItemRequests()){
            OrderItem orderItem = orderItemService.createOrderItem(itemRequest, order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        BigDecimal subtotalAmount = orderItems.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotalAmount(subtotalAmount);

        BigDecimal shippingFee = calculateShippingFee(request.getProvince(), subtotalAmount);
        order.setShippingFee(shippingFee);

        BigDecimal finalAmount
                = (subtotalAmount.multiply(BigDecimal.ONE.subtract(request.getDiscount()))).add(shippingFee);
        order.setFinalAmount(finalAmount);

        return OrderMapper.toOrderDetailResponse(order);
    }

    private BigDecimal calculateShippingFee(String provinceCode, BigDecimal subtotalAmount){
        if ("79".equals(provinceCode)){
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0){
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0){
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0){
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        }else {
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0){
                return BigDecimal.valueOf(35000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0){
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0){
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(1000000)) < 0){
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        }
    }
}
