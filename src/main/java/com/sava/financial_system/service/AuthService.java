package com.sava.financial_system.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // TODO: inject UserRepository when User is rebuilt

    public AuthService() {
        // empty constructor
    }

    public Object register(String email, String password, String firstName, String lastName) {
        // TODO: implement when User is rebuilt
        return null;
    }

    public Object authenticate(String email, String password) {
        // TODO: implement when User is rebuilt
        return null;
    }
}