package com.nestnow.service.impl;

import com.nestnow.dto.auth.AuthResponse;
import com.nestnow.dto.auth.LoginRequest;
import com.nestnow.dto.auth.RegisterRequest;
import com.nestnow.entity.User;
import com.nestnow.exception.ResourceAlreadyExistsException;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.AuthService;
import com.nestnow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new ResourceAlreadyExistsException(
                    "Email already exists"
            );
        }

        if (userRepository.existsByPhone(request.getPhone())) {

            throw new ResourceAlreadyExistsException(
                    "Phone already exists"
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getRole().name());
    }
}