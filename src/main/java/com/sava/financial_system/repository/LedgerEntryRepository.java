package com.sava.financial_system.repository;

import com.sava.financial_system.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    @Query("select coalesce(sum(e.amount), 0) from LedgerEntry e where e.accountId = :accountId and e.currencyCode = :currencyCode")
    BigDecimal sumByAccountAndCurrency(@Param("accountId") UUID accountId, @Param("currencyCode") String currencyCode);
}
