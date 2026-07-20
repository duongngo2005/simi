package com.ndd.simi_be.order.dto.request;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OrderRequest {
    @NotBlank(message = "Chưa chọn phường")
    private String ward;
    @NotBlank(message = "Chưa chọn tỉnh hoặc thành phố")
    private String province;
    @NotBlank(message = "Chưa chọn địa chỉ cụ thể")
    private String addressDetail;

    @NotNull(message = "Chi tiết đơn hàng không được thiếu")
    private List<OrderItemRequest> orderItemRequests;

    @NotBlank(message = "Bạn chưa nhập số điện thoại")
    private String recipientPhone;
    @NotBlank(message = "Bạn chưa nhập tên")
    private String recipientName;
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull(message = "Bạn chưa chọn phương thức thanh toán")
    private PaymentMethod paymentMethod;
}
