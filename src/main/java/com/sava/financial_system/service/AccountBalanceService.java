package com.sava.financial_system.service;

import com.sava.financial_system.entity.AccountBalance;
import com.sava.financial_system.repository.AccountBalanceRepository;
import com.sava.financial_system.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountBalanceService {
    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountRepository accountRepository;

    public AccountBalanceService(AccountBalanceRepository accountBalanceRepository,
                                 AccountRepository accountRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Initialize a balance for an account in a specific currency
     * Used when account is created or user adds new currency
     */
    public AccountBalance initializeBalance(UUID accountId, String currencyCode, BigDecimal initialAmount) {
        // Check if account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Check if balance already exists for this currency
        if (accountBalanceRepository.findByAccountIdAndCurrencyCode(accountId, currencyCode).isPresent()) {
            throw new IllegalArgumentException("Balance already exists for this currency");
        }

        // Create new balance
        AccountBalance balance = new AccountBalance();
        balance.setAccountId(accountId);
        balance.setCurrencyCode(currencyCode.toUpperCase());  // Normalize to uppercase
        balance.setAvailableBalance(initialAmount != null ? initialAmount : BigDecimal.ZERO);
        balance.setLockedBalance(BigDecimal.ZERO);

        return accountBalanceRepository.save(balance);
    }

    /**
     * Get balance for a specific account and currency
     */
    public AccountBalance getBalance(UUID accountId, String currencyCode) {
        return accountBalanceRepository.findByAccountIdAndCurrencyCode(accountId, currencyCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Balance not found for this currency"));
    }

    /**
     * Get all balances for an account
     */
    public List<AccountBalance> getAccountBalances(UUID accountId) {
        // Check if account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return accountBalanceRepository.findByAccountId(accountId);
    }

    /**
     * Add funds to available balance
     */
    public AccountBalance addFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        AccountBalance balance = getBalance(accountId, currencyCode);
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }

    /**
     * Deduct funds from available balance
     */
    public AccountBalance deductFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        AccountBalance balance = getBalance(accountId, currencyCode);

        // Check sufficient funds
        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        return accountBalanceRepository.save(balance);
    }

    /**
     * Lock funds (move from available to locked during pending transaction)
     */
    public AccountBalance lockFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        AccountBalance balance = getBalance(accountId, currencyCode);

        // Check sufficient available funds
        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds to lock");
        }

        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        balance.setLockedBalance(balance.getLockedBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }

    /**
     * Unlock funds (cancel pending transaction)
     */
    public AccountBalance unlockFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        AccountBalance balance = getBalance(accountId, currencyCode);

        // Check sufficient locked funds
        if (balance.getLockedBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient locked funds");
        }

        balance.setLockedBalance(balance.getLockedBalance().subtract(amount));
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }
}