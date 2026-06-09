package com.nestnow.service.impl;

import com.nestnow.dto.address.AddressResponse;
import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.booking.CreateBookingRequest;
import com.nestnow.dto.booking.UpdateBookingStatusRequest;
import com.nestnow.entity.*;
import com.nestnow.enums.BookingStatus;
import com.nestnow.enums.UserRole;
import com.nestnow.exception.BadRequestException;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.*;
import com.nestnow.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ServiceRepository serviceRepository;
    private final ProfessionalProfileRepository profileRepository;

    @Override
    public BookingResponse createBooking(
            String email,
            CreateBookingRequest request
    ) {

        User homeowner = findUser(email);

        if (homeowner.getRole() != UserRole.HOMEOWNER) {
            throw new BadRequestException(
                    "Only homeowners can create bookings"
            );
        }

        ServiceEntity service = serviceRepository
                .findById(request.getServiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service not found"
                        )
                );

        Address address = addressRepository
                .findById(request.getAddressId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found"
                        )
                );

        if (!address.getUser().getId().equals(homeowner.getId())) {
            throw new BadRequestException(
                    "Address does not belong to current user"
            );
        }

        ProfessionalProfile professional = pickProfessional();

        Booking booking = Booking.builder()
                .homeowner(homeowner)
                .professional(professional)
                .service(service)
                .address(address)
                .scheduledAt(resolveSchedule(request))
                .instantBooking(Boolean.TRUE.equals(
                        request.getInstantBooking()
                ))
                .estimatedAmount(service.getPrice())
                .customerNotes(request.getCustomerNotes())
                .status(professional == null
                        ? BookingStatus.REQUESTED
                        : BookingStatus.ACCEPTED)
                .updatedAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String email) {

        User user = findUser(email);

        return bookingRepository.findByHomeownerOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getProfessionalBookings(String email) {

        User user = findUser(email);
        ProfessionalProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professional profile not found"
                        )
                );

        return bookingRepository
                .findByProfessionalOrderByCreatedAtDesc(profile)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateStatus(
            String email,
            Long bookingId,
            UpdateBookingStatusRequest request
    ) {

        User user = findUser(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );

        if (!canUpdateBooking(user, booking)) {
            throw new BadRequestException(
                    "You cannot update this booking"
            );
        }

        if (request.getStatus() == null) {
            throw new BadRequestException("Booking status is required");
        }

        if (request.getStatus() == BookingStatus.CANCELLED) {
            booking.setCancellationReason(
                    request.getCancellationReason()
            );
        }

        booking.setStatus(request.getStatus());
        booking.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == BookingStatus.COMPLETED
                && booking.getProfessional() != null) {
            ProfessionalProfile professional =
                    booking.getProfessional();
            professional.setCompletedJobs(
                    professional.getCompletedJobs() + 1
            );
            profileRepository.save(professional);
        }

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {

        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    private ProfessionalProfile pickProfessional() {

        return profileRepository.findByVerifiedTrue()
                .stream()
                .max(Comparator.comparing(
                        ProfessionalProfile::getAverageRating
                ))
                .orElse(null);
    }

    private LocalDateTime resolveSchedule(
            CreateBookingRequest request
    ) {

        if (Boolean.TRUE.equals(request.getInstantBooking())) {
            return LocalDateTime.now().plusMinutes(30);
        }

        if (request.getScheduledAt() == null) {
            throw new BadRequestException(
                    "Scheduled time is required for non-instant bookings"
            );
        }

        return request.getScheduledAt();
    }

    private boolean canUpdateBooking(User user, Booking booking) {

        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        if (booking.getHomeowner() != null
                && booking.getHomeowner().getId().equals(user.getId())
                && booking.getStatus() != BookingStatus.COMPLETED) {
            return true;
        }

        return booking.getProfessional() != null
                && booking.getProfessional().getUser().getId()
                .equals(user.getId());
    }

    private BookingResponse mapToResponse(Booking booking) {

        return BookingResponse.builder()
                .id(booking.getId())
                .homeownerName(booking.getHomeowner().getFullName())
                .professionalName(booking.getProfessional() == null
                        ? null
                        : booking.getProfessional()
                        .getUser()
                        .getFullName())
                .serviceTitle(booking.getService().getTitle())
                .categoryName(booking.getService()
                        .getCategory()
                        .getName())
                .address(mapAddress(booking.getAddress()))
                .scheduledAt(booking.getScheduledAt())
                .instantBooking(booking.getInstantBooking())
                .status(booking.getStatus())
                .estimatedAmount(booking.getEstimatedAmount())
                .customerNotes(booking.getCustomerNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    private AddressResponse mapAddress(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
