package com.nestnow.service.impl;

import com.nestnow.dto.review.CreateReviewRequest;
import com.nestnow.dto.review.ReviewResponse;
import com.nestnow.entity.Booking;
import com.nestnow.entity.ProfessionalProfile;
import com.nestnow.entity.Review;
import com.nestnow.entity.User;
import com.nestnow.enums.BadgeTier;
import com.nestnow.enums.BookingStatus;
import com.nestnow.exception.BadRequestException;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.BookingRepository;
import com.nestnow.repository.ProfessionalProfileRepository;
import com.nestnow.repository.ReviewRepository;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ProfessionalProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse createReview(
            String email,
            CreateReviewRequest request
    ) {

        User homeowner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        if (!booking.getHomeowner().getId().equals(homeowner.getId())) {
            throw new BadRequestException(
                    "Only the booking homeowner can review"
            );
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException(
                    "Booking must be completed before review"
            );
        }

        if (booking.getProfessional() == null) {
            throw new BadRequestException(
                    "Booking has no assigned professional"
            );
        }

        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {
            throw new BadRequestException(
                    "Rating must be between 1 and 5"
            );
        }

        if (reviewRepository.existsByBooking(booking)) {
            throw new BadRequestException(
                    "Review already exists for this booking"
            );
        }

        Review review = Review.builder()
                .booking(booking)
                .homeowner(homeowner)
                .professional(booking.getProfessional())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        refreshProfessionalRating(booking.getProfessional());

        return mapToResponse(review);
    }

    @Override
    public List<ReviewResponse> getProfessionalReviews(
            Long professionalId
    ) {

        ProfessionalProfile professional = profileRepository
                .findById(professionalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professional not found"
                        )
                );

        return reviewRepository
                .findByProfessionalOrderByCreatedAtDesc(professional)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse flagReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found"
                        )
                );

        review.setFlagged(true);
        reviewRepository.save(review);

        return mapToResponse(review);
    }

    private void refreshProfessionalRating(
            ProfessionalProfile professional
    ) {

        List<Review> reviews = reviewRepository
                .findByProfessionalOrderByCreatedAtDesc(professional);

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        professional.setAverageRating(average);
        professional.setBadgeTier(resolveBadge(
                average,
                professional.getCompletedJobs()
        ));

        profileRepository.save(professional);
    }

    private BadgeTier resolveBadge(double average, int completedJobs) {

        if (completedJobs >= 100 && average >= 4.8) {
            return BadgeTier.PLATINUM;
        }

        if (completedJobs >= 50 && average >= 4.6) {
            return BadgeTier.GOLD;
        }

        if (completedJobs >= 10 && average >= 4.2) {
            return BadgeTier.SILVER;
        }

        return BadgeTier.BRONZE;
    }

    private ReviewResponse mapToResponse(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .homeownerName(review.getHomeowner().getFullName())
                .professionalName(review.getProfessional()
                        .getUser()
                        .getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .flagged(review.getFlagged())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
