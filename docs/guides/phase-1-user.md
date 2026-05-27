# Phase 1: User Subsystem

## User Entity
- `id` (UUID, @Id, @GeneratedValue)
- `email` (String, @Column(unique=true))
- `password` (String, hashed later)
- `firstName`, `lastName` (String)
- `role`, `status`, `kycStatus` (String)
- `createdAt`, `updatedAt` (LocalDateTime)

## UserRepository
- Extend `JpaRepository<User, UUID>`
- Add: `findByEmail(String email): Optional<User>`

## UserService
- Inject `UserRepository`
- Method: `createUser(User user): User`
    - Validate: email unique, email non-null, firstName/lastName non-empty
    - Save and return

## UserController
- Inject `UserService`
- Endpoint: `POST /users` (request body → User, response → User)

## DTOs
- `RegisterRequest`: email, password, firstName, lastName