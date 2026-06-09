package com.nestnow.service;

import com.nestnow.dto.review.CreateReviewRequest;
import com.nestnow.dto.review.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(
            String email,
            CreateReviewRequest request
    );

    List<ReviewResponse> getProfessionalReviews(Long professionalId);

    ReviewResponse flagReview(Long reviewId);
}
