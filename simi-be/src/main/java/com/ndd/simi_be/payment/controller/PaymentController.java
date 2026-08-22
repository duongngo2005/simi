package com.ndd.simi_be.payment.controller;

import com.ndd.simi_be.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> handleVnPayIpn(
            @RequestParam Map<String, String> params
    ){
        Map<String, String> result = paymentService.handleVnPayIpn(params);
        return ResponseEntity.ok(result);
    }
}
