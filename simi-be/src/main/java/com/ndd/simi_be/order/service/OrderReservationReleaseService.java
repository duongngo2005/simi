package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Releases products reserved by an order according to the consignment's state at release time.
 * A product must not become AVAILABLE again after its consignment has expired or entered settlement.
 */
@Service
@RequiredArgsConstructor
public class OrderReservationReleaseService {
    private final ConsignmentItemRepository consignmentItemRepository;

    public void release(Order order, LocalDateTime now) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            ConsignmentItem consignmentItem = consignmentItemRepository.findByProduct(product)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng tương ứng"));

            if (canBeSoldAgain(consignmentItem.getConsignment(), now)) {
                product.setProductStatus(ProductStatus.AVAILABLE);
                consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.ACTIVE);
            } else {
                product.setProductStatus(ProductStatus.EXPIRED);
                consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.EXPIRED);
            }
        }

        for (Payment payment : order.getPayments()) {
            if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
                payment.setPaymentStatus(PaymentStatus.CANCELLED);
            }
        }
    }

    private boolean canBeSoldAgain(Consignment consignment, LocalDateTime now) {
        return consignment.getConsignmentStatus() == ConsignmentStatus.ACTIVE
                && (consignment.getExpiryDate() == null || consignment.getExpiryDate().isAfter(now));
    }
}
