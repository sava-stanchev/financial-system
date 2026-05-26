package com.sava.financial_system.service;

import com.sava.financial_system.entity.Account;
import com.sava.financial_system.entity.User;
import com.sava.financial_system.repository.AccountRepository;
import com.sava.financial_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new account for a user
     * Validates user exists before creating account
     */
    public Account createAccount(UUID userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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