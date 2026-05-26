package com.sava.financial_system.repository;

import com.sava.financial_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    // Find all transactions for an account
    List<Transaction> findByAccountId(UUID accountId);

    // Find transactions for an account, ordered by newest first
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);

    // Find transactions by reference (e.g., all transactions for a transfer)
    List<Transaction> findByReferenceId(UUID referenceId);

    // Pagination support for large statement history
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);
}