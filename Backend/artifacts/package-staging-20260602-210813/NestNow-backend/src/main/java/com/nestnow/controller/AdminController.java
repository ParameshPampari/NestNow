package com.nestnow.controller;

import com.nestnow.dto.admin.AdminDashboardResponse;
import com.nestnow.dto.admin.VerifyProfessionalRequest;
import com.nestnow.dto.booking.BookingResponse;
import com.nestnow.dto.professional.ProfessionalProfileResponse;
import com.nestnow.dto.user.UserResponse;
import com.nestnow.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {

        return adminService.getDashboard();
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {

        return adminService.getUsers();
    }

    @GetMapping("/bookings")
    public List<BookingResponse> getBookings() {

        return adminService.getBookings();
    }

    @PutMapping("/professionals/{professionalId}/verify")
    public ProfessionalProfileResponse verifyProfessional(
            @PathVariable Long professionalId,
            @RequestBody VerifyProfessionalRequest request
    ) {

        return adminService.verifyProfessional(
                professionalId,
                request
        );
    }
}
