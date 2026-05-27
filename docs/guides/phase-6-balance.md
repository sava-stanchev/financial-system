# Phase 6: Account Balance Subsystem

## AccountBalance Entity
- `id` (UUID)
- `accountId` (UUID, FK to Account)
- `currencyCode` (String)
- `availableBalance` (BigDecimal)
- `lockedBalance` (BigDecimal)
- `totalBalance` (BigDecimal) [optional, can be calculated]
- Constraint: unique(accountId, currencyCode)
- `createdAt`, `updatedAt` (LocalDateTime)

## AccountBalanceRepository
- Extend `JpaRepository<AccountBalance, UUID>`
- Add: `findByAccountIdAndCurrencyCode(UUID, String): Optional<AccountBalance>`
- Add: `findByAccountId(UUID): List<AccountBalance>`

## AccountBalanceService
- Inject: `AccountBalanceRepository`, `AccountRepository`
- Method: `initializeBalance(accountId: UUID, currencyCode: String, initialAmount: BigDecimal): AccountBalance`
    - Validate: account exists, balance for this currency doesn't already exist
    - Create, save, return
- Method: `getBalance(accountId: UUID, currencyCode: String): AccountBalance`
    - Validate: balance exists or throw
- Method: `getAccountBalances(accountId: UUID): List<AccountBalance>`
    - Validate: account exists
    - Return all balances for account
- Method: `addFunds(accountId: UUID, currencyCode: String, amount: BigDecimal): AccountBalance`
    - Get balance, increase availableBalance, save, return
- Method: `deductFunds(accountId: UUID, currencyCode: String, amount: BigDecimal): AccountBalance`
    - Get balance, validate sufficient funds, decrease availableBalance, save, return
- Method: `lockFunds(...)`, `unlockFunds(...)` (optional, for advanced transfers)

## AccountBalanceController
- Inject `AccountBalanceService`
- Endpoints:
    - `POST /balances/initialize` (request: InitializeBalanceRequest)
    - `GET /balances/{accountId}/{currencyCode}` → AccountBalance
    - `GET /balances/account/{accountId}` → List<AccountBalance>
    - `POST /balances/add` (request: FundsOperationRequest)
    - `POST /balances/deduct` (request: FundsOperationRequest)

## DTOs
- `InitializeBalanceRequest`: accountId, currencyCode, initialAmount
- `FundsOperationRequest`: accountId, currencyCode, amount

## Key Concept
Balances track money per currency per account. addFunds/deductFunds are called by TransferService.