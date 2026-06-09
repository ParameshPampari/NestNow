package com.nestnow.dto.user;

import com.nestnow.enums.UserRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private UserRole role;

    private Boolean verified;
}