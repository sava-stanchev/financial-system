package com.sava.financial_system.repository;

import com.sava.financial_system.entity.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
    // Find balance for a specific account and currency
    Optional<AccountBalance> findByAccountIdAndCurrencyCode(UUID accountId, String currencyCode);

    // Find all balances for an account
    List<AccountBalance> findByAccountId(UUID accountId);
}