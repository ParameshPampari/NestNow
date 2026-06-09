package com.nestnow.service;

import com.nestnow.dto.user.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String email);
}