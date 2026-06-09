package com.nestnow.service;

import com.nestnow.dto.auth.AuthResponse;
import com.nestnow.dto.auth.LoginRequest;
import com.nestnow.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}