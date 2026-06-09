package com.nestnow.entity;

import jakarta.persistence.*;
import lombok.*;
import com.nestnow.enums.BadgeTier;

@Entity
@Table(name = "professional_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    private Integer experienceYears;

    private String serviceArea;

    private String profileImageUrl;

    @Builder.Default
    private Boolean verified = false;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer completedJobs = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BadgeTier badgeTier = BadgeTier.BRONZE;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
