package com.nestnow.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;

    private Long bookingId;

    private String homeownerName;

    private String professionalName;

    private Integer rating;

    private String comment;

    private Boolean flagged;

    private LocalDateTime createdAt;
}
