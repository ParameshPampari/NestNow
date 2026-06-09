package com.nestnow.dto.auth;

import com.nestnow.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private UserRole role;
}