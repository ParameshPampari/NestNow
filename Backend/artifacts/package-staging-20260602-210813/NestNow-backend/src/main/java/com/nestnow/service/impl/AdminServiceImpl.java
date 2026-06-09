package com.nestnow.service.impl;

import com.nestnow.dto.admin.AdminDashboardResponse;
import com.nestnow.dto.admin.VerifyProfessionalRequest;
import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.professional.ProfessionalProfileResponse;
import com.nestnow.dto.user.UserResponse;
import com.nestnow.entity.Booking;
import com.nestnow.entity.Payment;
import com.nestnow.entity.ProfessionalProfile;
import com.nestnow.entity.User;
import com.nestnow.enums.BookingStatus;
import com.nestnow.enums.PaymentStatus;
import com.nestnow.enums.UserRole;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.*;
import com.nestnow.service.AdminService;
import com.nestnow.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProfessionalProfileRepository profileRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    @Override
    public AdminDashboardResponse getDashboard() {

        List<Booking> bookings = bookingRepository.findAll();
        List<Payment> paidPayments = paymentRepository
                .findByStatus(PaymentStatus.PAID);

        double gmv = paidPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalProfessionals(profileRepository.count())
                .totalBookings(bookings.size())
                .activeBookings(bookings.stream()
                        .filter(booking -> booking.getStatus()
                                != BookingStatus.COMPLETED
                                && booking.getStatus()
                                != BookingStatus.CANCELLED)
                        .count())
                .completedBookings(bookings.stream()
                        .filter(booking -> booking.getStatus()
                                == BookingStatus.COMPLETED)
                        .count())
                .paidPayments(paidPayments.size())
                .grossMerchandiseValue(gmv)
                .build();
    }

    @Override
    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookings() {

        return bookingService.getAllBookings();
    }

    @Override
    public ProfessionalProfileResponse verifyProfessional(
            Long professionalId,
            VerifyProfessionalRequest request
    ) {

        ProfessionalProfile profile = profileRepository
                .findById(professionalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professional not found"
                        )
                );

        Boolean verified = Boolean.TRUE.equals(request.getVerified());
        profile.setVerified(verified);
        profile.getUser().setVerified(verified);

        profileRepository.save(profile);
        userRepository.save(profile.getUser());

        return mapProfile(profile);
    }

    private UserResponse mapUser(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .verified(user.getVerified())
                .build();
    }

    private ProfessionalProfileResponse mapProfile(
            ProfessionalProfile profile
    ) {

        User user = profile.getUser();

        return ProfessionalProfileResponse.builder()
                .id(profile.getId())
                .fullName(user.getFullName())
                .bio(profile.getBio())
                .experienceYears(profile.getExperienceYears())
                .serviceArea(profile.getServiceArea())
                .averageRating(profile.getAverageRating())
                .verified(profile.getVerified())
                .completedJobs(profile.getCompletedJobs())
                .badgeTier(profile.getBadgeTier())
                .build();
    }
}
