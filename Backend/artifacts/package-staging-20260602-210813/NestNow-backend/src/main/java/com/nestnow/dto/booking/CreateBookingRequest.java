package com.nestnow.dto.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {

    private Long serviceId;

    private Long addressId;

    private LocalDateTime scheduledAt;

    private Boolean instantBooking;

    private String customerNotes;
}
