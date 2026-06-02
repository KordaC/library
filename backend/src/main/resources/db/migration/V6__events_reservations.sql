CREATE TABLE event_registration (
    id              UUID PRIMARY KEY,
    event_id        UUID NOT NULL REFERENCES event(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    registered_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, user_id)
);

CREATE TABLE book_reservation (
    id              UUID PRIMARY KEY,
    card_id         UUID NOT NULL REFERENCES library_card(id),
    copy_id         UUID NOT NULL REFERENCES book_copy(id),
    book_id         UUID NOT NULL REFERENCES book(id),
    reserved_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX idx_book_reservation_card ON book_reservation(card_id, status);

INSERT INTO event (id, title, type, description, starts_at, capacity) VALUES
('33333333-3333-3333-3333-333333333302', 'Литературный клуб', 'CLUB', 'Обсуждение классики',
 CAST((CURRENT_DATE + 14) AS TIMESTAMP), 20);
