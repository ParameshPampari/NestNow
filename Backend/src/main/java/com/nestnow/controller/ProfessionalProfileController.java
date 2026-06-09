package com.nestnow.controller;

import com.nestnow.dto.professional.CreateProfessionalProfileRequest;
import com.nestnow.dto.professional.ProfessionalProfileResponse;
import com.nestnow.service.ProfessionalProfileService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalProfileController {

    private final ProfessionalProfileService service;

    @PostMapping("/profile")
    public ProfessionalProfileResponse createProfile(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody CreateProfessionalProfileRequest request
    ) {

        return service.createProfile(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/profile")
    public ProfessionalProfileResponse getMyProfile(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        return service.getMyProfile(
                authentication.getName()
        );
    }

    @GetMapping
    public List<ProfessionalProfileResponse>
    getVerifiedProfessionals() {

        return service.getVerifiedProfessionals();
    }

    @GetMapping("/{id}")
    public ProfessionalProfileResponse getProfessionalById(
            @PathVariable Long id
    ) {

        return service.getProfessionalById(id);
    }
}
