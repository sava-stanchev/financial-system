# Phase 3: Account Subsystem

## Account Entity
- `id` (UUID)
- `userId` (UUID, @Column(name="user_id"), FK to User)
- `type` (String, e.g., "CHECKING", "SAVINGS")
- `status` (String, e.g., "ACTIVE", "FROZEN")
- `accountNumber` (String, unique)
- `createdAt`, `updatedAt` (LocalDateTime)

## AccountRepository
- Extend `JpaRepository<Account, UUID>`
- Add: `findByUserId(UUID userId): List<Account>`

## AccountService
- Inject `AccountRepository`, `UserRepository`
- Method: `createAccount(userId: UUID, type: String): Account`
    - Validate: user exists (via UserRepository.findById)
    - Create account with userId, save, return
- Method: `getUserAccounts(userId: UUID): List<Account>`
    - Validate: user exists
    - Return AccountRepository.findByUserId(userId)
- Method: `getAccount(accountId: UUID): Account`
    - Find and return or throw
- Method: `updateAccountStatus(accountId: UUID, status: String): Account`
    - Find account, update status, save, return

## AccountController
- Inject `AccountService`
- Endpoints:
    - `POST /accounts` (request: CreateAccountRequest → response: Account)
    - `GET /accounts/user/{userId}` → List<Account>
    - `GET /accounts/{accountId}` → Account
    - `PUT /accounts/{accountId}/status` (request: UpdateAccountStatusRequest → response: Account)

## DTOs
- `CreateAccountRequest`: userId, type
- `UpdateAccountStatusRequest`: status

## Key Concept
Accounts belong to users (FK). Always validate user exists before creating account.