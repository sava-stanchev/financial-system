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

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest req) {
        User user = authService.register(
                req.getEmail(),
                req.getPassword(),
                req.getFirstName(),
                req.getLastName()
        );
        return new LoginResponse(user, "registration successful");
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        User user = authService.authenticate(req.getEmail(), req.getPassword());
        return new LoginResponse(user, "login successful");
    }
}