package com.nestnow.controller;

import com.nestnow.dto.payment.CreatePaymentRequest;
import com.nestnow.dto.payment.PaymentResponse;
import com.nestnow.service.PaymentService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreatePaymentRequest request
    ) {

        return paymentService.createPayment(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse getPaymentForBooking(
            @Parameter(hidden = true)
            Authentication authentication,
            @PathVariable Long bookingId
    ) {

        return paymentService.getPaymentForBooking(
                authentication.getName(),
                bookingId
        );
    }
}
