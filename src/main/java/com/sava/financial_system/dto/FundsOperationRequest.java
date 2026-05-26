package com.sava.financial_system.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class FundsOperationRequest {
    private UUID accountId;
    private String currencyCode;
    private BigDecimal amount;

    public FundsOperationRequest() {
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}