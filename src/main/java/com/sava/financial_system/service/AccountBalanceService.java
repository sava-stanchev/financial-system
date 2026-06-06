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
        validateAmount(initialAmount);

        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // check if balance already exists for this currency
        if (accountBalanceRepository.findByAccountIdAndCurrencyCode(accountId, currencyCode).isPresent())
            throw new IllegalArgumentException("Balance already exists for this currency");

        AccountBalance balance = new AccountBalance();
        balance.setAccountId(accountId);
        balance.setCurrencyCode(currencyCode.toUpperCase());
        balance.setAvailableBalance(initialAmount);
        balance.setLockedBalance(BigDecimal.ZERO);

        return accountBalanceRepository.save(balance);
    }

    public AccountBalance getBalance(UUID accountId, String currencyCode) {
        return accountBalanceRepository.findByAccountIdAndCurrencyCode(accountId, currencyCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Balance not found for this currency"));
    }

    public List<AccountBalance> getAccountBalances(UUID accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return accountBalanceRepository.findByAccountId(accountId);
    }

    public AccountBalance addFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        validateAmount(amount);
        AccountBalance balance = getBalance(accountId, currencyCode);
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }

    public AccountBalance deductFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        validateAmount(amount);
        AccountBalance balance = getBalance(accountId, currencyCode);
        if (balance.getAvailableBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds");
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        return accountBalanceRepository.save(balance);
    }

    // lock funds (move from available to locked during pending transaction)
    public AccountBalance lockFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        validateAmount(amount);
        AccountBalance balance = getBalance(accountId, currencyCode);
        if (balance.getAvailableBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds to lock");
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        balance.setLockedBalance(balance.getLockedBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }

    // unlock funds (cancel pending transaction)
    public AccountBalance unlockFunds(UUID accountId, String currencyCode, BigDecimal amount) {
        validateAmount(amount);
        AccountBalance balance = getBalance(accountId, currencyCode);
        if (balance.getLockedBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient locked funds");
        balance.setLockedBalance(balance.getLockedBalance().subtract(amount));
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        return accountBalanceRepository.save(balance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }
}