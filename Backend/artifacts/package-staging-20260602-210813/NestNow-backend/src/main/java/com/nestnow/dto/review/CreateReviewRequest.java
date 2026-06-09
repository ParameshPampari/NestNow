package com.nestnow.dto.review;

import lombok.Data;

@Data
public class CreateReviewRequest {

    private Long bookingId;

    private Integer rating;

    private String comment;
}
