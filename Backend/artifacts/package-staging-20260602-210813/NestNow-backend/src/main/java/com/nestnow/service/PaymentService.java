package com.nestnow.service;

import com.nestnow.dto.payment.CreatePaymentRequest;
import com.nestnow.dto.payment.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(
            String email,
            CreatePaymentRequest request
    );

    PaymentResponse getPaymentForBooking(
            String email,
            Long bookingId
    );
}
