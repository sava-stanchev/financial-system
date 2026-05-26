package com.sava.financial_system.controller;

import com.sava.financial_system.dto.FundsOperationRequest;
import com.sava.financial_system.dto.InitializeBalanceRequest;
import com.sava.financial_system.entity.AccountBalance;
import com.sava.financial_system.service.AccountBalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/balances")
public class AccountBalanceController {
    private final AccountBalanceService accountBalanceService;

    public AccountBalanceController(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }

    /**
     * Initialize a balance for an account in a specific currency
     * POST /balances/initialize
     */
    @PostMapping("/initialize")
    public AccountBalance initializeBalance(@RequestBody InitializeBalanceRequest request) {
        return accountBalanceService.initializeBalance(
                request.getAccountId(),
                request.getCurrencyCode(),
                request.getInitialAmount()
        );
    }

    /**
     * Get balance for a specific account and currency
     * GET /balances/{accountId}/{currencyCode}
     */
    @GetMapping("/{accountId}/{currencyCode}")
    public AccountBalance getBalance(@PathVariable UUID accountId, @PathVariable String currencyCode) {
        return accountBalanceService.getBalance(accountId, currencyCode);
    }

    /**
     * Get all balances for an account
     * GET /balances/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public List<AccountBalance> getAccountBalances(@PathVariable UUID accountId) {
        return accountBalanceService.getAccountBalances(accountId);
    }

    /**
     * Add funds to a balance
     * POST /balances/add
     */
    @PostMapping("/add")
    public AccountBalance addFunds(@RequestBody FundsOperationRequest request) {
        return accountBalanceService.addFunds(
                request.getAccountId(),
                request.getCurrencyCode(),
                request.getAmount()
        );
    }

    /**
     * Deduct funds from a balance
     * POST /balances/deduct
     */
    @PostMapping("/deduct")
    public AccountBalance deductFunds(@RequestBody FundsOperationRequest request) {
        return accountBalanceService.deductFunds(
                request.getAccountId(),
                request.getCurrencyCode(),
                request.getAmount()
        );
    }

    /**
     * Lock funds during a pending transaction
     * POST /balances/lock
     */
    @PostMapping("/lock")
    public AccountBalance lockFunds(@RequestBody FundsOperationRequest request) {
        return accountBalanceService.lockFunds(
                request.getAccountId(),
                request.getCurrencyCode(),
                request.getAmount()
        );
    }

    /**
     * Unlock funds if transaction is cancelled
     * POST /balances/unlock
     */
    @PostMapping("/unlock")
    public AccountBalance unlockFunds(@RequestBody FundsOperationRequest request) {
        return accountBalanceService.unlockFunds(
                request.getAccountId(),
                request.getCurrencyCode(),
                request.getAmount()
        );
    }
}