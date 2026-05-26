package com.sava.financial_system.dto;

public class UpdateAccountStatusRequest {
    private String status;  // ACTIVE, FROZEN, CLOSED

    public UpdateAccountStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}