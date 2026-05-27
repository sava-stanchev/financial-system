# Phase 5: Transfer Subsystem (Orchestrator)

## Transfer Entity
- `id` (UUID)
- `senderAccountId` (UUID, FK to Account)
- `receiverAccountId` (UUID, FK to Account)
- `currencyCode` (String)
- `amount` (BigDecimal)
- `note` (String)
- `status` (String, e.g., "COMPLETED", "PENDING")
- `createdAt`, `updatedAt` (LocalDateTime)

## TransferRepository
- Extend `JpaRepository<Transfer, UUID>`
- Add: `findBySenderAccountId(UUID): List<Transfer>`
- Add: `findByReceiverAccountId(UUID): List<Transfer>`

## TransferService
- Inject: `TransferRepository`, `AccountRepository`, `AccountBalanceService`, `TransactionService`
- Method: `createTransfer(senderAccountId: UUID, receiverAccountId: UUID, currencyCode: String, amount: BigDecimal, note: String): Transfer`
    - Validate: both accounts exist, are different accounts
    - Create Transfer record
    - Call: `AccountBalanceService.deductFunds(senderAccountId, currencyCode, amount)`
    - Call: `TransactionService.createTransaction(senderAccountId, "TRANSFER", "OUT", amount, currencyCode, transferId, note)`
    - Call: `AccountBalanceService.addFunds(receiverAccountId, currencyCode, amount)`
    - Call: `TransactionService.createTransaction(receiverAccountId, "TRANSFER", "IN", amount, currencyCode, transferId, note)`
    - Result: 1 Transfer + 2 Transactions + 2 Balance updates
- Method: `getSentTransfers(accountId: UUID): List<Transfer>`
- Method: `getReceivedTransfers(accountId: UUID): List<Transfer>`
- Method: `getAccountTransfers(accountId: UUID): List<Transfer>` (sent OR received)
- Method: `getTransfer(transferId: UUID): Transfer`

## TransferController
- Inject `TransferService`
- Endpoints:
    - `POST /transfers` (request: CreateTransferRequest)
    - `GET /transfers/sent/{accountId}` → List<Transfer>
    - `GET /transfers/received/{accountId}` → List<Transfer>
    - `GET /transfers/account/{accountId}` → List<Transfer>
    - `GET /transfers/{transferId}` → Transfer

## DTOs
- `CreateTransferRequest`: senderAccountId, receiverAccountId, currencyCode, amount, note

## Key Concept
Transfer orchestrates multiple services: validates accounts, updates balances, creates transaction records. One Transfer = two Transactions.