package com.nestnow.service;

import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.booking.CreateBookingRequest;
import com.nestnow.dto.booking.UpdateBookingStatusRequest;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(
            String email,
            CreateBookingRequest request
    );

    List<BookingResponse> getMyBookings(String email);

    List<BookingResponse> getProfessionalBookings(String email);

    BookingResponse updateStatus(
            String email,
            Long bookingId,
            UpdateBookingStatusRequest request
    );

    List<BookingResponse> getAllBookings();
}
