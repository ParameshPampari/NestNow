package com.nestnow.controller;

import com.nestnow.dto.user.UserResponse;
import com.nestnow.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @Parameter(hidden = true)
            Authentication authentication
    ) {

        String email = authentication.getName();

        return userService.getCurrentUser(email);
    }
}
