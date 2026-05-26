package com.sava.financial_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID senderAccountId;  // Account sending money

    @Column(nullable = false)
    private UUID receiverAccountId;  // Account receiving money

    @Column(nullable = false, length = 3)
    private String currencyCode;  // EUR, USD, GBP, etc.

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status = "COMPLETED";  // COMPLETED, PENDING, FAILED

    @Column(columnDefinition = "TEXT")
    private String note;  // Optional message/reference

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Transfer() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(UUID senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public UUID getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(UUID receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}