-- Демо: вход card=20001 / password=Demo1234
INSERT INTO user_account (id, email, phone, password_hash, status, created_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'demo@library.local', '+79001234567',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.G2oEBVHhKzJm2e', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO reader_profile (user_id, last_name, first_name, middle_name, birth_date, passport_series, passport_number, address) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Иванов', 'Иван', 'Иванович', '1990-05-15', '1234', '567890', 'г. Москва, ул. Примерная, 1');

INSERT INTO library_card (id, card_number, user_id, status, issued_at, registered_at) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '20001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ACTIVE', CURRENT_DATE, CURRENT_TIMESTAMP);

-- Привязка: card 10001, проверка birth_date 1990-05-15
INSERT INTO library_card (id, card_number, user_id, status, holder_last_name, holder_first_name, holder_birth_date, issued_at) VALUES
('cccccccc-cccc-cccc-cccc-ccccccccccc1', '10001', NULL, 'UNASSIGNED', 'Петров', 'Пётр', '1990-05-15', CURRENT_DATE),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', '10002', NULL, 'UNASSIGNED', 'Сидоров', 'Сидор', '1985-03-20', CURRENT_DATE);

INSERT INTO genre (id, name) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd01', 'Классика'),
('dddddddd-dddd-dddd-dddd-dddddddddd02', 'Фантастика');

INSERT INTO book (id, title, description, publication_year, isbn) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'Война и мир', 'Роман-эпопея', 1869, '978-5-17-000001-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'Мастер и Маргарита', 'Роман', 1967, '978-5-17-000002-2');

INSERT INTO book_genre (book_id, genre_id) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'dddddddd-dddd-dddd-dddd-dddddddddd01');

INSERT INTO book_copy (id, book_id, inventory_number, status) VALUES
('ffffffff-ffff-ffff-ffff-ffffffffff01', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'INV-001', 'ON_LOAN'),
('ffffffff-ffff-ffff-ffff-ffffffffff02', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'INV-002', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff03', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'INV-003', 'AVAILABLE');

INSERT INTO loan (id, card_id, copy_id, loan_date, due_date, returned_at, renewal_count, status) VALUES
('11111111-1111-1111-1111-111111111101', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'ffffffff-ffff-ffff-ffff-ffffffffff01',
 CURRENT_DATE - 10, CURRENT_DATE + 4, NULL, 0, 'ACTIVE');

INSERT INTO notification (id, user_id, type, title, body, read_flag, created_at) VALUES
('22222222-2222-2222-2222-222222222201', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'RETURN_SOON', 'Скоро срок возврата', 'Книга «Война и мир» — вернуть через 4 дня', false, CURRENT_TIMESTAMP);

INSERT INTO event (id, title, type, description, starts_at, capacity) VALUES
('33333333-3333-3333-3333-333333333301', 'Встреча с автором', 'MEETING', 'Обсуждение новинок',
 CAST((CURRENT_DATE + 7) AS TIMESTAMP), 30);
