package com.sava.financial_system.service;

import com.sava.financial_system.entity.Transfer;
import com.sava.financial_system.repository.TransferRepository;
import com.sava.financial_system.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final AccountBalanceService accountBalanceService;
    private final TransactionService transactionService;

    public TransferService(TransferRepository transferRepository,
                           AccountRepository accountRepository,
                           AccountBalanceService accountBalanceService,
                           TransactionService transactionService) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.accountBalanceService = accountBalanceService;
        this.transactionService = transactionService;
    }

    /**
     * Transfer money from one account to another
     */
    public Transfer createTransfer(UUID senderAccountId, UUID receiverAccountId,
                                   String currencyCode, BigDecimal amount, String note) {
        // 1. VALIDATION
        accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        if (senderAccountId.equals(receiverAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        accountBalanceService.getBalance(senderAccountId, currencyCode);

        // 2. CREATE TRANSFER RECORD
        Transfer transfer = new Transfer();
        transfer.setSenderAccountId(senderAccountId);
        transfer.setReceiverAccountId(receiverAccountId);
        transfer.setCurrencyCode(currencyCode.toUpperCase());
        transfer.setAmount(amount);
        transfer.setStatus("COMPLETED");
        transfer.setNote(note);

        Transfer savedTransfer = transferRepository.save(transfer);

        // 3. DEDUCT FROM SENDER
        accountBalanceService.deductFunds(senderAccountId, currencyCode, amount);

        // 4. CREATE SENDER TRANSACTION (OUT)
        transactionService.createTransaction(
                senderAccountId,
                "TRANSFER",
                "OUT",
                amount,
                currencyCode,
                savedTransfer.getId(),
                "Transfer to account " + receiverAccountId + (note != null ? ": " + note : "")
        );

        // 5. ADD TO RECEIVER
        accountBalanceService.addFunds(receiverAccountId, currencyCode, amount);

        // 6. CREATE RECEIVER TRANSACTION (IN)
        transactionService.createTransaction(
                receiverAccountId,
                "TRANSFER",
                "IN",
                amount,
                currencyCode,
                savedTransfer.getId(),
                "Transfer from account " + senderAccountId + (note != null ? ": " + note : "")
        );

        return savedTransfer;
    }

    /**
     * Get all transfers sent from an account
     */
    public List<Transfer> getSentTransfers(UUID accountId) {
        // Verify account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return transferRepository.findBySenderAccountId(accountId);
    }

    /**
     * Get all transfers received by an account
     */
    public List<Transfer> getReceivedTransfers(UUID accountId) {
        // Verify account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return transferRepository.findByReceiverAccountId(accountId);
    }

    /**
     * Get all transfers for an account (sent + received)
     */
    public List<Transfer> getAccountTransfers(UUID accountId) {
        // Verify account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return transferRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);
    }

    /**
     * Get a single transfer
     */
    public Transfer getTransfer(UUID transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found"));
    }
}