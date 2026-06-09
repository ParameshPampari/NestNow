package com.nestnow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    private String line1;

    private String line2;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
