package com.nestnow.service.impl;

import com.nestnow.dto.professional.CreateProfessionalProfileRequest;
import com.nestnow.dto.professional.ProfessionalProfileResponse;
import com.nestnow.entity.*;
import com.nestnow.enums.UserRole;
import com.nestnow.exception.*;
import com.nestnow.repository.*;
import com.nestnow.service.ProfessionalProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalProfileServiceImpl
        implements ProfessionalProfileService {

    private final UserRepository userRepository;

    private final ProfessionalProfileRepository profileRepository;

    @Override
    public ProfessionalProfileResponse createProfile(
            String email,
            CreateProfessionalProfileRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        if (user.getRole() != UserRole.PROFESSIONAL) {

            throw new BadRequestException(
                    "Only professionals can create profile"
            );
        }

        profileRepository.findByUser(user)
                .ifPresent(profile -> {
                    throw new ResourceAlreadyExistsException(
                            "Professional profile already exists"
                    );
                });

        ProfessionalProfile profile =
                ProfessionalProfile.builder()
                        .bio(request.getBio())
                        .experienceYears(
                                request.getExperienceYears()
                        )
                        .serviceArea(
                                request.getServiceArea()
                        )
                        .profileImageUrl(
                                request.getProfileImageUrl()
                        )
                        .user(user)
                        .build();

        profileRepository.save(profile);

        return mapToResponse(profile);
    }

    @Override
    public ProfessionalProfileResponse getMyProfile(
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        ProfessionalProfile profile =
                profileRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Profile not found"
                                ));

        return mapToResponse(profile);
    }

    @Override
    public List<ProfessionalProfileResponse>
    getVerifiedProfessionals() {

        return profileRepository.findByVerifiedTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProfessionalProfileResponse getProfessionalById(Long id) {

        ProfessionalProfile profile = profileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professional not found"
                        )
                );

        return mapToResponse(profile);
    }

    private ProfessionalProfileResponse mapToResponse(
            ProfessionalProfile profile
    ) {

        return ProfessionalProfileResponse.builder()
                .id(profile.getId())
                .fullName(
                        profile.getUser().getFullName()
                )
                .bio(profile.getBio())
                .experienceYears(
                        profile.getExperienceYears()
                )
                .serviceArea(
                        profile.getServiceArea()
                )
                .averageRating(
                        profile.getAverageRating()
                )
                .verified(
                        profile.getVerified()
                )
                .completedJobs(
                        profile.getCompletedJobs()
                )
                .badgeTier(
                        profile.getBadgeTier()
                )
                .build();
    }
}
