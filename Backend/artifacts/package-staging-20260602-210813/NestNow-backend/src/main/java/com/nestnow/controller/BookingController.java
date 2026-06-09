package com.nestnow.controller;

import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.booking.CreateBookingRequest;
import com.nestnow.dto.booking.UpdateBookingStatusRequest;
import com.nestnow.service.BookingService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreateBookingRequest request
    ) {

        return bookingService.createBooking(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        return bookingService.getMyBookings(
                authentication.getName()
        );
    }

    @GetMapping("/professional")
    public List<BookingResponse> getProfessionalBookings(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        return bookingService.getProfessionalBookings(
                authentication.getName()
        );
    }

    @PutMapping("/{bookingId}/status")
    public BookingResponse updateStatus(
            @Parameter(hidden = true)
            Authentication authentication,
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingStatusRequest request
    ) {

        return bookingService.updateStatus(
                authentication.getName(),
                bookingId,
                request
        );
    }
}
