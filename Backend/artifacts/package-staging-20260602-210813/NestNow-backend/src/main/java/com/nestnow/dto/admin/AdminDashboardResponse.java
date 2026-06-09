package com.nestnow.dto.admin;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalProfessionals;

    private long totalBookings;

    private long activeBookings;

    private long completedBookings;

    private long paidPayments;

    private Double grossMerchandiseValue;
}
