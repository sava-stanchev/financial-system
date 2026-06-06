package com.sava.financial_system.service;

import com.sava.financial_system.entity.Transfer;
import com.sava.financial_system.repository.TransferRepository;
import com.sava.financial_system.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Transfer createTransfer(UUID senderAccountId, UUID receiverAccountId,
                                   String currencyCode, BigDecimal amount, String note) {
        if (amount == null ||  amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        currencyCode = normalizeCurrency(currencyCode);
        verifyAccountsExist(senderAccountId, receiverAccountId);
        if (senderAccountId.equals(receiverAccountId))
            throw new IllegalArgumentException("Cannot transfer to the same account");

        // create transfer in PENDING state
        Transfer transfer = createPendingTransfer(senderAccountId,receiverAccountId, currencyCode, amount, note);

        // debit sender
        accountBalanceService.deductFunds(senderAccountId, currencyCode, amount);

        // sender transaction (OUT)
        transactionService.createTransaction(
                senderAccountId,
                "TRANSFER",
                "OUT",
                amount,
                currencyCode,
                transfer.getId(),
                "Transfer to account " + receiverAccountId + (note != null ? ": " + note : "")
        );

        // credit receiver
        accountBalanceService.addFunds(receiverAccountId, currencyCode, amount);

        // receiver transaction (IN)
        transactionService.createTransaction(
                receiverAccountId,
                "TRANSFER",
                "IN",
                amount,
                currencyCode,
                transfer.getId(),
                "Transfer from account " + senderAccountId + (note != null ? ": " + note : "")
        );

        transfer.setStatus("COMPLETED");
        return transferRepository.save(transfer);
    }

    public List<Transfer> getSentTransfers(UUID accountId) {
        verifyAccountExists(accountId);
        return transferRepository.findBySenderAccountId(accountId);
    }

    public List<Transfer> getReceivedTransfers(UUID accountId) {
        verifyAccountExists(accountId);
        return transferRepository.findByReceiverAccountId(accountId);
    }

    public List<Transfer> getAccountTransfers(UUID accountId) {
        verifyAccountExists(accountId);
        return transferRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);
    }

    public Transfer getTransfer(UUID transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found"));
    }

    private void verifyAccountsExist(UUID senderId, UUID receiverId) {
        accountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
        accountRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
    }

    private void verifyAccountExists(UUID accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private Transfer createPendingTransfer(UUID senderId, UUID receiverId, String currencyCode,
                                           BigDecimal amount, String note) {
        Transfer transfer = new Transfer();
        transfer.setSenderAccountId(senderId);
        transfer.setReceiverAccountId(receiverId);
        transfer.setCurrencyCode(currencyCode);
        transfer.setAmount(amount);
        transfer.setNote(note);
        transfer.setStatus("PENDING");

        return transferRepository.save(transfer);
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank())
            throw new IllegalArgumentException("Currency code is required");

        return currencyCode.toUpperCase();
    }
}