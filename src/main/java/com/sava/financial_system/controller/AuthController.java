package com.sava.financial_system.controller;

import com.sava.financial_system.dto.LoginRequest;
import com.sava.financial_system.dto.AuthResponse;
import com.sava.financial_system.dto.RegisterRequest;
import com.sava.financial_system.entity.User;
import com.sava.financial_system.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        User user = authService.register(
                req.getEmail(),
                req.getPassword(),
                req.getFirstName(),
                req.getLastName()
        );

        String accessTok = authService.generateAccessToken(user);
        String refreshTok =  authService.generateRefreshToken(user);

        AuthResponse res = new AuthResponse(user, "Registration successful");
        res.setAccessToken(accessTok);
        res.setRefreshToken(refreshTok);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        User user = authService.authenticate(req.getEmail(), req.getPassword());

        String accessTok = authService.generateAccessToken(user);
        String refreshTok =  authService.generateRefreshToken(user);

        AuthResponse res = new AuthResponse(user, "Login successful");
        res.setAccessToken(accessTok);
        res.setRefreshToken(refreshTok);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}