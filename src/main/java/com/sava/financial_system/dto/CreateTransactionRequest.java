package com.sava.financial_system.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateTransactionRequest {
    private UUID accountId;
    private String type;  // DEPOSIT, WITHDRAWAL, TRANSFER, EXCHANGE, CARD_PAYMENT, REFUND
    private String direction;  // IN or OUT
    private BigDecimal amount;
    private String currencyCode;
    private UUID referenceId;  // Optional: links to transfer, exchange, etc.
    private String description;

    public CreateTransactionRequest() {
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}