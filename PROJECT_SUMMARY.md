# Financial System - Project Summary

## Overview
A Revolut-style financial backend built with Spring Boot, featuring user management, accounts, transfers, transactions, balances, and JWT authentication.

---

## User Subsystem

**Entities & Repositories:**
- `User` entity (UUID id, email unique, password hashed, role, status, kycStatus, timestamps)
- `UserRepository` (findByEmail custom method)

**Services:**
- `UserService` → createUser(User)

**Controllers & DTOs:**
- `UserController` → POST /users
- `RegisterRequest` (email, password, firstName, lastName)

---

## JWT Authentication

**Services:**
- `JwtTokenProvider` → generateAccessToken, generateRefreshToken, validateToken, extractClaims
- `AuthService` → register, authenticate, generateAccessToken, generateRefreshToken

**Controllers & DTOs:**
- `AuthController` → POST /auth/register, POST /auth/login, POST /auth/logout
- `AuthResponse` (id, email, firstName, lastName, role, status, kycStatus, accessToken, refreshToken, message)
- `LoginRequest` (email, password)

**Config:**
- `JwtAuthenticationFilter` (extracts & validates JWT on every request)
- `CustomAuthenticationEntryPoint` (returns 401 for missing/invalid token)
- `SecurityConfiguration` (stateless, protected endpoints, password encoder bean)

**Config File:**
- `application-local.properties` (jwt.secret ≥64 chars, jwt.expiration, jwt.refresh-expiration)

---

## Account Subsystem

**Entities & Repositories:**
- `Account` entity (UUID id, userId FK, type, status, accountNumber unique, timestamps)
- `AccountRepository` (findByUserId custom method)

**Services:**
- `AccountService` → createAccount, getUserAccounts, getAccount, updateAccountStatus
  - Validates user exists before creating account

**Controllers & DTOs:**
- `AccountController` → POST /accounts, GET /accounts/user/{userId}, GET /accounts/{accountId}, PUT /accounts/{accountId}/status
- `CreateAccountRequest` (userId, type)
- `UpdateAccountStatusRequest` (status)

---

## Transaction Subsystem

**Entities & Repositories:**
- `Transaction` entity (UUID id, accountId FK, type, direction IN/OUT, amount, currencyCode, description, referenceId, balanceId optional, timestamps)
- `TransactionRepository` (findByAccountId, findByAccountIdOrderByCreatedAtDesc, findByReferenceId custom methods)

**Services:**
- `TransactionService` → createTransaction (2 overloads: with/without referenceId), getAccountTransactions, getTransactionsByReference, getTransaction
  - Validates account exists before creating

**Controllers & DTOs:**
- `TransactionController` → POST /transactions, GET /transactions/account/{accountId}, GET /transactions/reference/{referenceId}, GET /transactions/{transactionId}
- `CreateTransactionRequest` (accountId, type, direction, amount, currencyCode, referenceId optional, description)

---

## Transfer Subsystem (Orchestrator)

**Entities & Repositories:**
- `Transfer` entity (UUID id, senderAccountId FK, receiverAccountId FK, currencyCode, amount, note, status, timestamps)
- `TransferRepository` (findBySenderAccountId, findByReceiverAccountId custom methods)

**Services:**
- `TransferService` → createTransfer, getSentTransfers, getReceivedTransfers, getAccountTransfers, getTransfer
  - **Orchestrates multi-step workflow**: validates both accounts → creates Transfer → deducts sender balance → creates sender transaction → adds receiver balance → creates receiver transaction

**Controllers & DTOs:**
- `TransferController` → POST /transfers, GET /transfers/sent/{accountId}, GET /transfers/received/{accountId}, GET /transfers/account/{accountId}, GET /transfers/{transferId}
- `CreateTransferRequest` (senderAccountId, receiverAccountId, currencyCode, amount, note)

**Key Behavior:**
- 1 Transfer = 2 Transactions (sender OUT, receiver IN)
- 1 Transfer = 2 Balance updates (deduct + add)

---

## AccountBalance Subsystem

**Entities & Repositories:**
- `AccountBalance` entity (UUID id, accountId FK, currencyCode, availableBalance, lockedBalance, totalBalance, timestamps, unique constraint: accountId + currencyCode)
- `AccountBalanceRepository` (findByAccountIdAndCurrencyCode, findByAccountId custom methods)

**Services:**
- `AccountBalanceService` → initializeBalance, getBalance, getAccountBalances, addFunds, deductFunds, lockFunds (optional), unlockFunds (optional)
  - Validates account exists before operations
  - Used by TransferService to update balances

**Controllers & DTOs:**
- `AccountBalanceController` → POST /balances/initialize, GET /balances/{accountId}/{currencyCode}, GET /balances/account/{accountId}, POST /balances/add, POST /balances/deduct
- `InitializeBalanceRequest` (accountId, currencyCode, initialAmount)
- `FundsOperationRequest` (accountId, currencyCode, amount)

---

## Database Schema

```
users
├── id (UUID, PK)
├── email (VARCHAR unique, NOT NULL)
├── password (VARCHAR)
├── firstName (VARCHAR)
├── lastName (VARCHAR)
├── role (VARCHAR, default 'USER')
├── status (VARCHAR, default 'ACTIVE')
├── kycStatus (VARCHAR, default 'PENDING')
├── createdAt (TIMESTAMP)
└── updatedAt (TIMESTAMP)

account
├── id (UUID, PK)
├── userId (UUID, FK → users.id)
├── type (VARCHAR)
├── status (VARCHAR)
├── accountNumber (VARCHAR unique)
├── createdAt (TIMESTAMP)
└── updatedAt (TIMESTAMP)

account_balance
├── id (UUID, PK)
├── accountId (UUID, FK → account.id)
├── currencyCode (VARCHAR)
├── availableBalance (DECIMAL)
├── lockedBalance (DECIMAL)
├── totalBalance (DECIMAL)
├── createdAt (TIMESTAMP)
├── updatedAt (TIMESTAMP)
└── Constraint: unique(accountId, currencyCode)

transaction
├── id (UUID, PK)
├── accountId (UUID, FK → account.id)
├── type (VARCHAR) -- DEPOSIT, WITHDRAWAL, TRANSFER
├── direction (VARCHAR) -- IN, OUT
├── amount (DECIMAL)
├── currencyCode (VARCHAR)
├── description (VARCHAR)
├── referenceId (UUID) -- links to Transfer
├── balanceId (UUID, FK → account_balance.id, optional)
├── createdAt (TIMESTAMP)
└── updatedAt (TIMESTAMP)

transfer
├── id (UUID, PK)
├── senderAccountId (UUID, FK → account.id)
├── receiverAccountId (UUID, FK → account.id)
├── currencyCode (VARCHAR)
├── amount (DECIMAL)
├── note (VARCHAR)
├── status (VARCHAR)
├── createdAt (TIMESTAMP)
└── updatedAt (TIMESTAMP)
```

---

## Security & Access Control

**Public Endpoints (no auth required):**
- POST /auth/register
- POST /auth/login
- POST /auth/logout
- POST /users
- GET /api/health

**Protected Endpoints (require valid JWT token):**
- GET /accounts/** (authenticated users)
- POST /accounts/** (authenticated users)
- GET /balances/** (authenticated users)
- POST /balances/** (authenticated users)
- GET /transactions/** (authenticated users)
- POST /transactions/** (authenticated users)
- GET /transfers/** (authenticated users)
- POST /transfers/** (authenticated users)

**Token Details:**
- Access Token: 15 minute expiry (used for requests)
- Refresh Token: 7 day expiry (used to get new access token)
- Algorithm: HS512 (HMAC with SHA-512)
- Secret: ≥64 characters (stored in application-local.properties)

---

## File Structure

```
src/main/java/com/sava/financial_system/
├── entity/
│   ├── User.java
│   ├── Account.java
│   ├── AccountBalance.java
│   ├── Transaction.java
│   └── Transfer.java
├── repository/
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   ├── AccountBalanceRepository.java
│   ├── TransactionRepository.java
│   └── TransferRepository.java
├── service/
│   ├── UserService.java
│   ├── AuthService.java
│   ├── JwtTokenProvider.java
│   ├── AccountService.java
│   ├── AccountBalanceService.java
│   ├── TransactionService.java
│   └── TransferService.java
├── controller/
│   ├── UserController.java
│   ├── AuthController.java
│   ├── AccountController.java
│   ├── AccountBalanceController.java
│   ├── TransactionController.java
│   └── TransferController.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── CreateAccountRequest.java
│   ├── UpdateAccountStatusRequest.java
│   ├── CreateTransactionRequest.java
│   ├── CreateTransferRequest.java
│   ├── InitializeBalanceRequest.java
│   └── FundsOperationRequest.java
├── config/
│   ├── SecurityConfiguration.java
│   ├── GlobalExceptionHandler.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomAuthenticationEntryPoint.java
└── FinancialSystemApplication.java
```

---

## Next Phases (Planned)

- Transaction Safety & Consistency (optimistic/pessimistic locking, isolation levels)
- Event-Driven Transfers (Spring ApplicationEvents, async processing)
- Caching & Query Optimization (Redis, pagination, indexes)
- API Validation & Error Handling (Bean validation, Swagger)
- Monitoring & Observability (Structured logging, metrics)
