package com.nestnow.repository;

import com.nestnow.entity.Subscription;
import com.nestnow.entity.User;
import com.nestnow.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findFirstByUserAndStatusOrderByCreatedAtDesc(
            User user,
            SubscriptionStatus status
    );

    List<Subscription> findByUserOrderByCreatedAtDesc(User user);
}
