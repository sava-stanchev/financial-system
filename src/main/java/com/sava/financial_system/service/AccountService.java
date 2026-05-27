package com.sava.financial_system.service;

import com.sava.financial_system.entity.Account;
import com.sava.financial_system.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Create a new account for a user
     * Validates user exists before creating account
     */
    public Account createAccount(UUID userId, String type) {
        Account account = new Account();
        account.setUserId(userId);
        account.setType(type != null ? type : "PERSONAL");
        account.setStatus("ACTIVE");

        return accountRepository.save(account);
    }

    /**
     * Get all accounts owned by a user
     */
    public List<Account> getUserAccounts(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Get a specific account by ID
     */
    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Close/freeze an account
     */
    public Account updateAccountStatus(UUID accountId, String newStatus) {
        Account account = getAccount(accountId);
        account.setStatus(newStatus);
        return accountRepository.save(account);
    }
}