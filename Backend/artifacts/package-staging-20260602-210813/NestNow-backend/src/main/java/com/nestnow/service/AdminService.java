package com.nestnow.service;

import com.nestnow.dto.admin.AdminDashboardResponse;
import com.nestnow.dto.admin.VerifyProfessionalRequest;
import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.professional.ProfessionalProfileResponse;
import com.nestnow.dto.user.UserResponse;

import java.util.List;

public interface AdminService {

    AdminDashboardResponse getDashboard();

    List<UserResponse> getUsers();

    List<BookingResponse> getBookings();

    ProfessionalProfileResponse verifyProfessional(
            Long professionalId,
            VerifyProfessionalRequest request
    );
}
