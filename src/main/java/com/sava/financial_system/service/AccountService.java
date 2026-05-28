package com.sava.financial_system.service;

import com.sava.financial_system.entity.Account;
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

    public Account createAccount(UUID userId, String type) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("user not found");
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setType(type != null ? type : "PERSONAL");
        account.setStatus("ACTIVE");

        return accountRepository.save(account);
    }

    public List<Account> getUserAccounts(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("user not found");
        }

        return accountRepository.findByUserId(userId);
    }

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // close/freeze account
    public Account updateAccountStatus(UUID accountId, String newStatus) {
        Account account = getAccount(accountId);
        account.setStatus(newStatus);
        return accountRepository.save(account);
    }
}