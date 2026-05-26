package com.sava.financial_system.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class InitializeBalanceRequest {
    private UUID accountId;
    private String currencyCode;
    private BigDecimal initialAmount;

    public InitializeBalanceRequest() {
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

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }
}