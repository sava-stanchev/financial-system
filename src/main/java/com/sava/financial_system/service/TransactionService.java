package com.sava.financial_system.service;

import com.sava.financial_system.entity.Transaction;
import com.sava.financial_system.repository.TransactionRepository;
import com.sava.financial_system.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Create a transaction record
     * Called whenever money moves
     */
    public Transaction createTransaction(UUID accountId, String type, String direction,
                                         BigDecimal amount, String currencyCode,
                                         String description) {
        return createTransaction(accountId, type, direction, amount, currencyCode, null, description);
    }

    /**
     * Create a transaction with reference (e.g., transfer, exchange)
     */
    public Transaction createTransaction(UUID accountId, String type, String direction,
                                         BigDecimal amount, String currencyCode,
                                         UUID referenceId, String description) {
        // Verify account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Validate direction
        if (!direction.equals("IN") && !direction.equals("OUT")) {
            throw new IllegalArgumentException("Direction must be IN or OUT");
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setType(type.toUpperCase());
        transaction.setDirection(direction.toUpperCase());
        transaction.setAmount(amount);
        transaction.setCurrencyCode(currencyCode.toUpperCase());
        transaction.setReferenceId(referenceId);
        transaction.setDescription(description);
        transaction.setStatus("COMPLETED");

        return transactionRepository.save(transaction);
    }

    /**
     * Get all transactions for an account (newest first)
     */
    public List<Transaction> getAccountTransactions(UUID accountId) {
        // Verify account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    /**
     * Get transactions related to a specific reference (e.g., transfer)
     */
    public List<Transaction> getTransactionsByReference(UUID referenceId) {
        return transactionRepository.findByReferenceId(referenceId);
    }

    /**
     * Get a single transaction
     */
    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }
}