package com.nestnow.entity;

import com.nestnow.enums.SubscriptionPlan;
import com.nestnow.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private Boolean autoRenew = false;

    @Builder.Default
    private Integer inspectionsRemaining = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
