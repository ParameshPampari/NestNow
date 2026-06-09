package com.nestnow.dto.booking;

import com.nestnow.enums.BookingStatus;
import lombok.Data;

@Data
public class UpdateBookingStatusRequest {

    private BookingStatus status;

    private String cancellationReason;
}
