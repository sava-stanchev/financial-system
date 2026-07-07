package com.sava.financial_system.service;

import com.sava.financial_system.entity.LedgerEntry;
import com.sava.financial_system.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LedgerService {
    private final LedgerEntryRepository ledgerRepo;

    public LedgerService(LedgerEntryRepository ledgerRepo) {
        this.ledgerRepo = ledgerRepo;
    }

    @Transactional
    public LedgerEntry createEntry(UUID accountId, String currencyCode, BigDecimal amount,
                                   String direction, String entryType, UUID referenceId) {
        LedgerEntry e = new LedgerEntry();
        e.setAccountId(accountId);
        e.setCurrencyCode(currencyCode.toUpperCase());
        e.setAmount(amount);
        e.setDirection(direction);
        e.setEntryType(entryType);
        e.setReferenceId(referenceId);
        e.setCreatedAt(LocalDateTime.now());
        return ledgerRepo.save(e);
    }

    public BigDecimal getLedgerBalance(UUID accountId, String currencyCode) {
        return ledgerRepo.sumByAccountAndCurrency(accountId, currencyCode.toUpperCase());
    }
}
