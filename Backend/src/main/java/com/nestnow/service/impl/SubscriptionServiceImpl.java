package com.nestnow.service.impl;

import com.nestnow.dto.subscription.CreateSubscriptionRequest;
import com.nestnow.dto.subscription.SubscriptionResponse;
import com.nestnow.entity.Subscription;
import com.nestnow.entity.User;
import com.nestnow.enums.SubscriptionPlan;
import com.nestnow.enums.SubscriptionStatus;
import com.nestnow.exception.BadRequestException;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.SubscriptionRepository;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl
        implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    public SubscriptionResponse subscribe(
            String email,
            CreateSubscriptionRequest request
    ) {

        User user = findUser(email);

        subscriptionRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(
                        user,
                        SubscriptionStatus.ACTIVE
                )
                .ifPresent(subscription -> {
                    throw new BadRequestException(
                            "User already has an active subscription"
                    );
                });

        SubscriptionPlan plan = request.getPlan() == null
                ? SubscriptionPlan.BASIC
                : request.getPlan();

        LocalDate start = LocalDate.now();
        LocalDate end = Boolean.TRUE.equals(request.getAnnual())
                ? start.plusYears(1)
                : start.plusMonths(1);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(start)
                .endDate(end)
                .autoRenew(Boolean.TRUE.equals(
                        request.getAutoRenew()
                ))
                .inspectionsRemaining(plan == SubscriptionPlan.PREMIUM
                        ? 2
                        : 1)
                .build();

        subscriptionRepository.save(subscription);

        return mapToResponse(subscription);
    }

    @Override
    public List<SubscriptionResponse> getMySubscriptions(
            String email
    ) {

        User user = findUser(email);

        return subscriptionRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponse cancel(
            String email,
            Long subscriptionId
    ) {

        User user = findUser(email);
        Subscription subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found"
                        )
                );

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "Subscription does not belong to current user"
            );
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        subscriptionRepository.save(subscription);

        return mapToResponse(subscription);
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    private SubscriptionResponse mapToResponse(
            Subscription subscription
    ) {

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .plan(subscription.getPlan())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .inspectionsRemaining(
                        subscription.getInspectionsRemaining()
                )
                .build();
    }
}
