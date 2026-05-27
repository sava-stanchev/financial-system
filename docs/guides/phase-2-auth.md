# Phase 2: Auth Subsystem

## AuthService
- Inject `UserRepository`, `BCryptPasswordEncoder` (create as @Bean in config or new)
- Method: `register(email: String, password: String, firstName: String, lastName: String): User`
    - Validate: email not already registered
    - Hash password with BCrypt
    - Create User, save via UserRepository, return
- Method: `authenticate(email: String, password: String): User`
    - Find user by email (UserRepository.findByEmail)
    - Verify password matches (BCryptPasswordEncoder.matches)
    - Return user or throw exception if not found or password wrong

## AuthController
- Inject `AuthService`
- Endpoint: `POST /auth/register` (request: RegisterRequest → response: LoginResponse)
- Endpoint: `POST /auth/login` (request: LoginRequest → response: LoginResponse)

## DTOs
- `LoginRequest`: email, password
- `LoginResponse`: id, email, firstName, lastName, role, status, kycStatus, message

## Key Concept
Auth doesn't create users; it calls UserService methods. Auth validates credentials.