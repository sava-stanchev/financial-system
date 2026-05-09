package com.sava.financial_system.controller;

import com.sava.financial_system.dto.LoginRequest;
import com.sava.financial_system.dto.LoginResponse;
import com.sava.financial_system.dto.RegisterRequest;
import com.sava.financial_system.entity.User;
import com.sava.financial_system.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
        );
        return new LoginResponse(user, "Registration successful");
    }

    /**
     * Login user
     * POST /auth/login
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        return new LoginResponse(user, "Login successful");
    }
}