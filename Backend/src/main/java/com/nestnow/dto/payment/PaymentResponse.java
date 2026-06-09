package com.nestnow.dto.payment;

import com.nestnow.enums.PaymentMethod;
import com.nestnow.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long id;

    private Long bookingId;

    private Double amount;

    private PaymentMethod method;

    private PaymentStatus status;

    private String gatewayReference;

    private LocalDateTime paidAt;
}
