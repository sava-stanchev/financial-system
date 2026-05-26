package com.sava.financial_system.repository;

import com.sava.financial_system.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    // Find all transfers sent from an account
    List<Transfer> findBySenderAccountId(UUID senderAccountId);

    // Find all transfers received by an account
    List<Transfer> findByReceiverAccountId(UUID receiverAccountId);

    // Find all transfers (sent + received) for an account
    List<Transfer> findBySenderAccountIdOrReceiverAccountId(UUID senderAccountId, UUID receiverAccountId);
}