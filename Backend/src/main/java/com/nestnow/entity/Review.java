package com.nestnow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "homeowner_id")
    private User homeowner;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private ProfessionalProfile professional;

    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Builder.Default
    private Boolean flagged = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
