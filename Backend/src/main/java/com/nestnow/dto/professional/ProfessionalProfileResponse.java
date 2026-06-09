package com.nestnow.dto.professional;

import com.nestnow.enums.BadgeTier;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalProfileResponse {

    private Long id;

    private String fullName;

    private String bio;

    private Integer experienceYears;

    private String serviceArea;

    private Double averageRating;

    private Boolean verified;

    private Integer completedJobs;

    private BadgeTier badgeTier;
}
