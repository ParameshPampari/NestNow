package com.nestnow.service;

import com.nestnow.dto.subscription.CreateSubscriptionRequest;
import com.nestnow.dto.subscription.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse subscribe(
            String email,
            CreateSubscriptionRequest request
    );

    List<SubscriptionResponse> getMySubscriptions(String email);

    SubscriptionResponse cancel(
            String email,
            Long subscriptionId
    );
}
