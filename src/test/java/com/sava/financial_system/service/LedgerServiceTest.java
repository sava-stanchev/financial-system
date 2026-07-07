package com.sava.financial_system.service;

import com.sava.financial_system.entity.Account;
import com.sava.financial_system.entity.LedgerEntry;
import com.sava.financial_system.repository.AccountRepository;
import com.sava.financial_system.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LedgerServiceTest {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    LedgerEntryRepository ledgerEntryRepository;

    @Test
    public void createEntriesAndSum() {
        Account account = new Account();
        account.setUserId(UUID.randomUUID());
        account.setType("PERSONAL");
        account.setStatus("ACTIVE");
        account = accountRepository.save(account);

        UUID accountId = account.getId();
        String cur = "USD";

        LedgerEntry e1 = ledgerService.createEntry(accountId, cur, new BigDecimal("100.00"), "CREDIT", "TEST", null);
        LedgerEntry e2 = ledgerService.createEntry(accountId, cur, new BigDecimal("-25.50"), "DEBIT", "TEST", null);

        BigDecimal balance = ledgerService.getLedgerBalance(accountId, cur);
        assertThat(balance).isEqualByComparingTo(new BigDecimal("74.50"));

        ledgerEntryRepository.deleteById(e1.getId());
        ledgerEntryRepository.deleteById(e2.getId());
        accountRepository.deleteById(account.getId());
    }
}
