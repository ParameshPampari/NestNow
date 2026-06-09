package com.nestnow.controller;

import com.nestnow.dto.review.CreateReviewRequest;
import com.nestnow.dto.review.ReviewResponse;
import com.nestnow.service.ReviewService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse createReview(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreateReviewRequest request
    ) {

        return reviewService.createReview(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/professional/{professionalId}")
    public List<ReviewResponse> getProfessionalReviews(
            @PathVariable Long professionalId
    ) {

        return reviewService.getProfessionalReviews(professionalId);
    }

    @PutMapping("/{reviewId}/flag")
    public ReviewResponse flagReview(@PathVariable Long reviewId) {

        return reviewService.flagReview(reviewId);
    }
}
