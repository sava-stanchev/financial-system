package com.sava.financial_system.controller;

import com.sava.financial_system.dto.CreateTransferRequest;
import com.sava.financial_system.entity.Transfer;
import com.sava.financial_system.service.TransferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Create a transfer (send money)
     * POST /transfers
     */
    @PostMapping
    public Transfer createTransfer(@RequestBody CreateTransferRequest request) {
        return transferService.createTransfer(
                request.getSenderAccountId(),
                request.getReceiverAccountId(),
                request.getCurrencyCode(),
                request.getAmount(),
                request.getNote()
        );
    }

    /**
     * Get all transfers sent from an account
     * GET /transfers/sent/{accountId}
     */
    @GetMapping("/sent/{accountId}")
    public List<Transfer> getSentTransfers(@PathVariable UUID accountId) {
        return transferService.getSentTransfers(accountId);
    }

    /**
     * Get all transfers received by an account
     * GET /transfers/received/{accountId}
     */
    @GetMapping("/received/{accountId}")
    public List<Transfer> getReceivedTransfers(@PathVariable UUID accountId) {
        return transferService.getReceivedTransfers(accountId);
    }

    /**
     * Get all transfers for an account (sent + received)
     * GET /transfers/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public List<Transfer> getAccountTransfers(@PathVariable UUID accountId) {
        return transferService.getAccountTransfers(accountId);
    }

    /**
     * Get a single transfer
     * GET /transfers/{transferId}
     */
    @GetMapping("/{transferId}")
    public Transfer getTransfer(@PathVariable UUID transferId) {
        return transferService.getTransfer(transferId);
    }
}