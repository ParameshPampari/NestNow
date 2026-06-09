package com.nestnow.repository;

import com.nestnow.entity.ProfessionalProfile;
import com.nestnow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProfessionalProfileRepository
        extends JpaRepository<ProfessionalProfile, Long> {

    Optional<ProfessionalProfile> findByUser(User user);

    List<ProfessionalProfile> findByVerifiedTrue();
}
