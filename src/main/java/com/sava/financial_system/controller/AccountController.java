package com.sava.financial_system.controller;

import com.sava.financial_system.dto.CreateAccountRequest;
import com.sava.financial_system.dto.UpdateAccountStatusRequest;
import com.sava.financial_system.entity.Account;
import com.sava.financial_system.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Create a new account
     * POST /accounts
     */
    @PostMapping
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request.getUserId(), request.getType());
    }

    /**
     * Get all accounts for a user
     * GET /accounts/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<Account> getUserAccounts(@PathVariable UUID userId) {
        return accountService.getUserAccounts(userId);
    }

    /**
     * Get a specific account
     * GET /accounts/{accountId}
     */
    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable UUID accountId) {
        return accountService.getAccount(accountId);
    }

    /**
     * Update account status
     * PUT /accounts/{accountId}/status
     */
    @PutMapping("/{accountId}/status")
    public Account updateAccountStatus(
            @PathVariable UUID accountId,
            @RequestBody UpdateAccountStatusRequest request) {
        return accountService.updateAccountStatus(accountId, request.getStatus());
    }
}