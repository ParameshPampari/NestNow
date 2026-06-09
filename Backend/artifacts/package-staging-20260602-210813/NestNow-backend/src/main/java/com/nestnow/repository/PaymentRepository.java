package com.nestnow.repository;

import com.nestnow.entity.Booking;
import com.nestnow.entity.Payment;
import com.nestnow.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBooking(Booking booking);

    boolean existsByBooking(Booking booking);

    List<Payment> findByStatus(PaymentStatus status);
}
