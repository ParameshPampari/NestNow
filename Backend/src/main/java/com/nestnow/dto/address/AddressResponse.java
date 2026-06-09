package com.nestnow.dto.address;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {

    private Long id;

    private String label;

    private String line1;

    private String line2;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;
}
