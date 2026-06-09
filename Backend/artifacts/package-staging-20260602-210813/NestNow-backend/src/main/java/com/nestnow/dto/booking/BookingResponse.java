package com.nestnow.dto.booking;

import com.nestnow.dto.address.AddressResponse;
import com.nestnow.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long id;

    private String homeownerName;

    private String professionalName;

    private String serviceTitle;

    private String categoryName;

    private AddressResponse address;

    private LocalDateTime scheduledAt;

    private Boolean instantBooking;

    private BookingStatus status;

    private Double estimatedAmount;

    private String customerNotes;

    private LocalDateTime createdAt;
}
