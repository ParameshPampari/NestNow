package com.nestnow.dto.professional;

import lombok.Data;

@Data
public class CreateProfessionalProfileRequest {

    private String bio;

    private Integer experienceYears;

    private String serviceArea;

    private String profileImageUrl;
}