package com.nestnow.service.impl;

import com.nestnow.dto.user.UserResponse;
import com.nestnow.entity.User;
import com.nestnow.exception.ResourceNotFoundException;
import com.nestnow.repository.UserRepository;
import com.nestnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .verified(user.getVerified())
                .build();
    }
}
