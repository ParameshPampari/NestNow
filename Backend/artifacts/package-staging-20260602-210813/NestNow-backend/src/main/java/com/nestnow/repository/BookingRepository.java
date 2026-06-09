package com.nestnow.repository;

import com.nestnow.entity.Booking;
import com.nestnow.entity.ProfessionalProfile;
import com.nestnow.entity.User;
import com.nestnow.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByHomeownerOrderByCreatedAtDesc(User homeowner);

    List<Booking> findByProfessionalOrderByCreatedAtDesc(
            ProfessionalProfile professional
    );

    List<Booking> findByStatus(BookingStatus status);
}
