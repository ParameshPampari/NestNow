package com.nestnow.entity;

import com.nestnow.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homeowner_id")
    private User homeowner;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private ProfessionalProfile professional;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private LocalDateTime scheduledAt;

    @Builder.Default
    private Boolean instantBooking = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.REQUESTED;

    private Double estimatedAmount;

    @Column(length = 1000)
    private String customerNotes;

    @Column(length = 1000)
    private String cancellationReason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
