package com.ndd.simi_be.order.mapper;

import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.dto.response.OrderSummaryResponse;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.payment.mapper.PaymentMapper;
import com.ndd.simi_be.product.entity.ProductImage;

public class OrderMapper {
    public static OrderDetailResponse toOrderDetailResponse(Order order){
        return OrderDetailResponse.builder()
                .id(order.getId())
                .recipientName(order.getRecipientName())
                .acceptedByName(
                        order.getAcceptedBy() == null
                        ? null
                        : order.getAcceptedBy().getFullName()
                )
                .recipientPhone(order.getRecipientPhone())
                .ward(order.getWard())
                .province(order.getProvince())
                .addressDetail(order.getAddressDetail())
                .subtotalAmount(order.getSubtotalAmount())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .finalAmount(order.getFinalAmount())
                .orderStatus(order.getOrderStatus().name())
                .orderChannel(order.getOrderChannel().name())
                .createdDate(order.getCreatedDate())
                .orderItems(
                        order.getOrderItems().stream().map(OrderItemMapper::toOrderItemResponse).toList()
                )
                .paymentResponses(
                    order.getPayments().stream().map(PaymentMapper::toPaymentResponse).toList()
                )
                .build();
    }

    public static OrderSummaryResponse toOrderSummaryResponse(Order order){
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus().name())
                .finalAmount(order.getFinalAmount())
                .createdDate(order.getCreatedDate())
                .firstItemName(order.getOrderItems().getFirst().getProduct().getName())
                .firstItemThumbnail(
                        order.getOrderItems().getFirst().getProduct().getProductImages().stream()
                                .filter(ProductImage::isThumbnail)
                                .findFirst().isEmpty() ? null
                                : order.getOrderItems().getFirst().getProduct().getProductImages().stream()
                                .filter(ProductImage::isThumbnail)
                                .findFirst().get().getImageUrl()
                )
                .totalItem(order.getOrderItems().size())
                .build();
    }
}
