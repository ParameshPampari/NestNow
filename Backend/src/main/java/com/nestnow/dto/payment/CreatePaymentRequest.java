package com.nestnow.dto.payment;

import com.nestnow.enums.PaymentMethod;
import lombok.Data;

@Data
public class CreatePaymentRequest {

    private Long bookingId;

    private PaymentMethod method;

    private String gatewayReference;
}
