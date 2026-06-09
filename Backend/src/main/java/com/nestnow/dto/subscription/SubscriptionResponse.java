package com.nestnow.dto.subscription;

import com.nestnow.enums.SubscriptionPlan;
import com.nestnow.enums.SubscriptionStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {

    private Long id;

    private SubscriptionPlan plan;

    private SubscriptionStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    private Integer inspectionsRemaining;
}
