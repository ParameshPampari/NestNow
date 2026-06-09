package com.nestnow.dto.subscription;

import com.nestnow.enums.SubscriptionPlan;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {

    private SubscriptionPlan plan;

    private Boolean annual;

    private Boolean autoRenew;
}
