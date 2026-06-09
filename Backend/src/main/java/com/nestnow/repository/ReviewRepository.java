package com.nestnow.repository;

import com.nestnow.entity.Booking;
import com.nestnow.entity.ProfessionalProfile;
import com.nestnow.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBooking(Booking booking);

    List<Review> findByProfessionalOrderByCreatedAtDesc(
            ProfessionalProfile professional
    );
}
