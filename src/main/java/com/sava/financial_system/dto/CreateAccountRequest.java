package com.sava.financial_system.dto;

import java.util.UUID;

public class CreateAccountRequest {
    private UUID userId;
    private String type;  // PERSONAL, BUSINESS, SAVINGS, etc.

    public CreateAccountRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}