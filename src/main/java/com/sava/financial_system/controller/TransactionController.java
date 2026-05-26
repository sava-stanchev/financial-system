package com.sava.financial_system.controller;

import com.sava.financial_system.dto.CreateTransactionRequest;
import com.sava.financial_system.entity.Transaction;
import com.sava.financial_system.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create a transaction record
     * POST /transactions
     */
    @PostMapping
    public Transaction createTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(
                request.getAccountId(),
                request.getType(),
                request.getDirection(),
                request.getAmount(),
                request.getCurrencyCode(),
                request.getReferenceId(),
                request.getDescription()
        );
    }

    /**
     * Get all transactions for an account (statement)
     * GET /transactions/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public List<Transaction> getAccountTransactions(@PathVariable UUID accountId) {
        return transactionService.getAccountTransactions(accountId);
    }

    /**
     * Get transactions linked to a reference (e.g., transfer)
     * GET /transactions/reference/{referenceId}
     */
    @GetMapping("/reference/{referenceId}")
    public List<Transaction> getTransactionsByReference(@PathVariable UUID referenceId) {
        return transactionService.getTransactionsByReference(referenceId);
    }

    /**
     * Get a single transaction
     * GET /transactions/{transactionId}
     */
    @GetMapping("/{transactionId}")
    public Transaction getTransaction(@PathVariable UUID transactionId) {
        return transactionService.getTransaction(transactionId);
    }
}