package com.sava.financial_system.service;

import com.sava.financial_system.entity.Account;
import com.sava.financial_system.entity.LedgerEntry;
import com.sava.financial_system.entity.User;
import com.sava.financial_system.repository.AccountRepository;
import com.sava.financial_system.repository.LedgerEntryRepository;
import com.sava.financial_system.repository.UserRepository;
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
    UserRepository userRepository;

    @Autowired
    AccountBalanceService accountBalanceService;

    @Autowired
    TransferService transferService;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    LedgerEntryRepository ledgerEntryRepository;

    @Test
    public void createEntriesAndSum() {
        User user = new User();
        user.setEmail("ledger-test@example.com");
        user.setPasswordHash("test-hash");
        user.setFirstName("Ledger");
        user.setLastName("Test");
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setKycStatus("PENDING");
        user = userRepository.save(user);

        Account account = new Account();
        account.setUserId(user.getId());
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
        userRepository.deleteById(user.getId());
    }

    @Test
    public void transferCreatesLedgerEntriesForSenderAndReceiver() {
        User senderUser = new User();
        senderUser.setEmail("transfer-sender-" + UUID.randomUUID() + "@example.com");
        senderUser.setPasswordHash("test-hash");
        senderUser.setFirstName("Transfer");
        senderUser.setLastName("Sender");
        senderUser.setRole("USER");
        senderUser.setStatus("ACTIVE");
        senderUser.setKycStatus("PENDING");
        senderUser = userRepository.save(senderUser);

        User receiverUser = new User();
        receiverUser.setEmail("transfer-receiver-" + UUID.randomUUID() + "@example.com");
        receiverUser.setPasswordHash("test-hash");
        receiverUser.setFirstName("Transfer");
        receiverUser.setLastName("Receiver");
        receiverUser.setRole("USER");
        receiverUser.setStatus("ACTIVE");
        receiverUser.setKycStatus("PENDING");
        receiverUser = userRepository.save(receiverUser);

        Account senderAccount = new Account();
        senderAccount.setUserId(senderUser.getId());
        senderAccount.setType("PERSONAL");
        senderAccount.setStatus("ACTIVE");
        senderAccount = accountRepository.save(senderAccount);

        Account receiverAccount = new Account();
        receiverAccount.setUserId(receiverUser.getId());
        receiverAccount.setType("PERSONAL");
        receiverAccount.setStatus("ACTIVE");
        receiverAccount = accountRepository.save(receiverAccount);

        accountBalanceService.initializeBalance(senderAccount.getId(), "USD", new BigDecimal("200.00"));
        accountBalanceService.initializeBalance(receiverAccount.getId(), "USD", new BigDecimal("100.00"));

        transferService.createTransfer(
                senderAccount.getId(),
                receiverAccount.getId(),
                "USD",
                new BigDecimal("75.00"),
                "test transfer"
        );

        assertThat(ledgerService.getLedgerBalance(senderAccount.getId(), "USD"))
                .isEqualByComparingTo(new BigDecimal("-75.00"));
        assertThat(ledgerService.getLedgerBalance(receiverAccount.getId(), "USD"))
                .isEqualByComparingTo(new BigDecimal("75.00"));
    }
}