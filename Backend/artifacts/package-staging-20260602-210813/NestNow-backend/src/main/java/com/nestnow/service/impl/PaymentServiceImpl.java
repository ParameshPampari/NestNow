package com.nestnow.service.impl;

import com.nestnow.dto.payment.CreatePaymentRequest;
import com.nestnow.dto.payment.PaymentResponse;
import com.nestnow.entity.Booking;
import com.nestnow.entity.Payment;
import com.nestnow.entity.User;
import com.nestnow.enums.PaymentMethod;
import com.nestnow.enums.PaymentStatus;
import com.nestnow.enums.UserRole;
import com.nestnow.exception.BadRequestException;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.BookingRepository;
import com.nestnow.repository.PaymentRepository;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentResponse createPayment(
            String email,
            CreatePaymentRequest request
    ) {

        User user = findUser(email);
        Booking booking = findBooking(request.getBookingId());

        if (!booking.getHomeowner().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "Only the homeowner can pay for this booking"
            );
        }

        if (paymentRepository.existsByBooking(booking)) {
            throw new BadRequestException(
                    "Payment already exists for this booking"
            );
        }

        PaymentMethod method = request.getMethod() == null
                ? PaymentMethod.CASH
                : request.getMethod();

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getEstimatedAmount())
                .method(method)
                .gatewayReference(request.getGatewayReference())
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentForBooking(
            String email,
            Long bookingId
    ) {

        User user = findUser(email);
        Booking booking = findBooking(bookingId);

        if (!canViewPayment(user, booking)) {
            throw new BadRequestException(
                    "You cannot view this payment"
            );
        }

        Payment payment = paymentRepository.findByBooking(booking)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        return mapToResponse(payment);
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    private Booking findBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        )
                );
    }

    private boolean canViewPayment(User user, Booking booking) {

        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        if (booking.getHomeowner().getId().equals(user.getId())) {
            return true;
        }

        return booking.getProfessional() != null
                && booking.getProfessional().getUser().getId()
                .equals(user.getId());
    }

    private PaymentResponse mapToResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .gatewayReference(payment.getGatewayReference())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
