CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id         UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255),
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    role       VARCHAR(50)              DEFAULT 'USER',
    status     VARCHAR(50)              DEFAULT 'ACTIVE',
    kyc_status VARCHAR(50)              DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE accounts
(
    id             UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    user_id        UUID NOT NULL REFERENCES users (id),
    type           VARCHAR(50),
    status         VARCHAR(50),
    account_number VARCHAR(100) UNIQUE,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at     TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE account_balances
(
    id                UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    account_id        UUID           NOT NULL REFERENCES accounts (id),
    currency_code     VARCHAR(8)     NOT NULL,
    available_balance NUMERIC(20, 6) NOT NULL  DEFAULT 0,
    locked_balance    NUMERIC(20, 6) NOT NULL  DEFAULT 0,
    version           BIGINT         NOT NULL  DEFAULT 0,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT uq_account_currency UNIQUE (account_id, currency_code)
);

CREATE TABLE ledger_entries
(
    id            UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    account_id    UUID            NOT NULL REFERENCES accounts (id),
    currency_code VARCHAR(8)      NOT NULL,
    amount        NUMERIC(30, 12) NOT NULL,
    direction     VARCHAR(10)     NOT NULL,
    entry_type    VARCHAR(50),
    reference_id  UUID,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE transactions
(
    id              UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    account_id      UUID            NOT NULL REFERENCES accounts (id),
    ledger_entry_id UUID REFERENCES ledger_entries (id),
    type            VARCHAR(50),
    direction       VARCHAR(10),
    amount          NUMERIC(30, 12) NOT NULL,
    currency_code   VARCHAR(8)      NOT NULL,
    description     TEXT,
    reference_id    UUID,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE transfers
(
    id                  UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    sender_account_id   UUID            NOT NULL REFERENCES accounts (id),
    receiver_account_id UUID            NOT NULL REFERENCES accounts (id),
    currency_code       VARCHAR(8)      NOT NULL,
    amount              NUMERIC(30, 12) NOT NULL,
    note                TEXT,
    status              VARCHAR(50)              DEFAULT 'PENDING',
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE outbox_entry
(
    id              UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    aggregate_type  VARCHAR(100),
    aggregate_id    UUID,
    topic           VARCHAR(200),
    key             VARCHAR(200),
    payload         TEXT,
    status          VARCHAR(20)              DEFAULT 'PENDING',
    attempts        INT                      DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT now(),
    last_attempt_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE fx_rate
(
    id             UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    base_currency  VARCHAR(8),
    quote_currency VARCHAR(8),
    rate           NUMERIC(30, 12),
    source         VARCHAR(255),
    valid_from     TIMESTAMP WITH TIME ZONE DEFAULT now(),
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_ledger_account ON ledger_entries (account_id);
CREATE INDEX idx_outbox_status ON outbox_entry (status);