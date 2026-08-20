package com.ndd.simi_be.email.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderConfirmationEmailData {
    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String addressDetail;
    private String ward;
    private String province;
    private String paymentMethod;
    private LocalDateTime createdDate;

    private List<ItemData> items;

    private BigDecimal subtotalAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;

    @Data
    @Builder
    public static class ItemData{
        private String productName;
        private String size;
        private String color;
        private String thumbnailUrl;
        private BigDecimal unitPrice;
    }
}
