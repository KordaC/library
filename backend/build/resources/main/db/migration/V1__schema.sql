CREATE TABLE user_account (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) UNIQUE,
    phone           VARCHAR(20) UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reader_profile (
    user_id         UUID PRIMARY KEY REFERENCES user_account(id) ON DELETE CASCADE,
    last_name       VARCHAR(100) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    birth_date      DATE NOT NULL,
    passport_series VARCHAR(4),
    passport_number VARCHAR(6),
    address         VARCHAR(500),
    photo_url       VARCHAR(500),
    updated_at      TIMESTAMP
);

CREATE TABLE library_card (
    id              UUID PRIMARY KEY,
    card_number     VARCHAR(5) NOT NULL UNIQUE
        CHECK (card_number ~ '^[0-9]{5}$'),
    user_id         UUID UNIQUE REFERENCES user_account(id) ON DELETE SET NULL,
    status          VARCHAR(20) NOT NULL,
    holder_last_name   VARCHAR(100),
    holder_first_name  VARCHAR(100),
    holder_birth_date  DATE,
    issued_at       DATE NOT NULL,
    registered_at   TIMESTAMP
);

CREATE TABLE genre (
    id              UUID PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE book (
    id              UUID PRIMARY KEY,
    title           VARCHAR(500) NOT NULL,
    description     VARCHAR(2000),
    publication_year INT,
    isbn            VARCHAR(20)
);

CREATE TABLE book_genre (
    book_id         UUID NOT NULL REFERENCES book(id) ON DELETE CASCADE,
    genre_id        UUID NOT NULL REFERENCES genre(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

CREATE TABLE book_copy (
    id              UUID PRIMARY KEY,
    book_id         UUID NOT NULL REFERENCES book(id) ON DELETE CASCADE,
    inventory_number VARCHAR(50) NOT NULL UNIQUE,
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE loan (
    id              UUID PRIMARY KEY,
    card_id         UUID NOT NULL REFERENCES library_card(id),
    copy_id         UUID NOT NULL REFERENCES book_copy(id),
    loan_date       DATE NOT NULL,
    due_date        DATE NOT NULL,
    returned_at     DATE,
    renewal_count   INT NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL
);

CREATE TABLE registration_request (
    id              UUID PRIMARY KEY,
    last_name       VARCHAR(100) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    birth_date      DATE NOT NULL,
    passport_series VARCHAR(4) NOT NULL,
    passport_number VARCHAR(6) NOT NULL,
    address         VARCHAR(500) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    status          VARCHAR(30) NOT NULL,
    created_card_id UUID REFERENCES library_card(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    type            VARCHAR(30) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    body            VARCHAR(1000) NOT NULL,
    read_flag       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event (
    id              UUID PRIMARY KEY,
    title           VARCHAR(300) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    description     VARCHAR(2000),
    starts_at       TIMESTAMP NOT NULL,
    capacity        INT NOT NULL DEFAULT 50
);

CREATE INDEX idx_loan_card_active ON loan(card_id);
