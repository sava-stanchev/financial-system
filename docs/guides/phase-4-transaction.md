# Phase 4: Transaction Subsystem

## Transaction Entity
- `id` (UUID)
- `accountId` (UUID, FK to Account)
- `type` (String, e.g., "DEPOSIT", "WITHDRAWAL", "TRANSFER")
- `direction` (String, "IN" or "OUT")
- `amount` (BigDecimal)
- `currencyCode` (String)
- `description` (String)
- `referenceId` (UUID, links to Transfer/other operations)
- `balanceId` (UUID, optional FK to AccountBalance)
- `createdAt`, `updatedAt` (LocalDateTime)

## TransactionRepository
- Extend `JpaRepository<Transaction, UUID>`
- Add: `findByAccountId(UUID): List<Transaction>`
- Add: `findByAccountIdOrderByCreatedAtDesc(UUID): List<Transaction>` (recent first)
- Add: `findByReferenceId(UUID): List<Transaction>`

## TransactionService
- Inject `TransactionRepository`, `AccountRepository`
- Method: `createTransaction(accountId: UUID, type: String, direction: String, amount: BigDecimal, currencyCode: String, description: String): Transaction`
    - Validate: account exists, direction is "IN" or "OUT"
    - Create, save, return
- Method: `createTransaction(..., referenceId: UUID, description: String): Transaction`
    - Same as above but include referenceId
- Method: `getAccountTransactions(accountId: UUID): List<Transaction>`
    - Validate: account exists
    - Return most recent first
- Method: `getTransactionsByReference(referenceId: UUID): List<Transaction>`
    - Return all transactions linked to this reference
- Method: `getTransaction(transactionId: UUID): Transaction`

## TransactionController
- Inject `TransactionService`
- Endpoints:
    - `POST /transactions` (request: CreateTransactionRequest)
    - `GET /transactions/account/{accountId}` → List<Transaction>
    - `GET /transactions/reference/{referenceId}` → List<Transaction>
    - `GET /transactions/{transactionId}` → Transaction

## DTOs
- `CreateTransactionRequest`: accountId, type, direction, amount, currencyCode, referenceId (optional), description

## Key Concept
Transactions are audit trail; they link to accounts and optionally to other operations (Transfer) via referenceId.