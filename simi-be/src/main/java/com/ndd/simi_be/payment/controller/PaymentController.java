package com.ndd.simi_be.payment.controller;

import com.ndd.simi_be.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> simulatePayment(
            @PathVariable("id") Long paymentId,
            @RequestParam boolean success
    ){
        paymentService.simulatePayment(paymentId, success);
        return ResponseEntity.noContent().build();
    }
}
