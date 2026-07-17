package com.ndd.simi_be.payment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.payment.dto.PaymentRequest;
import com.ndd.simi_be.payment.dto.PaymentResponse;
import com.ndd.simi_be.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request){
        ApiResponse<PaymentResponse> response = ApiResponse.<PaymentResponse>builder()
                .status(201)
                .message("Tạo thông tin thanh toán thành công")
                .body(paymentService.createPayment(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> simulatePayment(
            @PathVariable("id") Long paymentId,
            @RequestParam boolean success
    ){
        paymentService.simulatePayment(paymentId, success);
        return ResponseEntity.noContent().build();
    }
}
