package com.nestnow.controller;

import com.nestnow.dto.subscription.CreateSubscriptionRequest;
import com.nestnow.dto.subscription.SubscriptionResponse;
import com.nestnow.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionResponse subscribe(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreateSubscriptionRequest request
    ) {

        return subscriptionService.subscribe(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/my")
    public List<SubscriptionResponse> getMySubscriptions(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        return subscriptionService.getMySubscriptions(
                authentication.getName()
        );
    }

    @PutMapping("/{subscriptionId}/cancel")
    public SubscriptionResponse cancel(
            @Parameter(hidden = true)
            Authentication authentication,
            @PathVariable Long subscriptionId
    ) {

        return subscriptionService.cancel(
                authentication.getName(),
                subscriptionId
        );
    }
}
