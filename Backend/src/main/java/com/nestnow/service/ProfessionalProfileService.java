package com.nestnow.service;

import com.nestnow.dto.professional.CreateProfessionalProfileRequest;
import com.nestnow.dto.professional.ProfessionalProfileResponse;

import java.util.List;

public interface ProfessionalProfileService {

    ProfessionalProfileResponse createProfile(
            String email,
            CreateProfessionalProfileRequest request
    );

    ProfessionalProfileResponse getMyProfile(
            String email
    );

    List<ProfessionalProfileResponse> getVerifiedProfessionals();

    ProfessionalProfileResponse getProfessionalById(Long id);
}
