INSERT INTO genre (id, name) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd03', 'Детектив'),
('dddddddd-dddd-dddd-dddd-dddddddddd04', 'Фантастика и фэнтези'),
('dddddddd-dddd-dddd-dddd-dddddddddd05', 'Поэзия'),
('dddddddd-dddd-dddd-dddd-dddddddddd06', 'История'),
('dddddddd-dddd-dddd-dddd-dddddddddd07', 'Драма'),
('dddddddd-dddd-dddd-dddd-dddddddddd08', 'Детская литература');

INSERT INTO book (id, title, description, publication_year, isbn, author_name) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'Преступление и наказание', 'Психологический роман', 1866, '978-5-17-000003-3', 'Ф. М. Достоевский'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'Евгений Онегин', 'Роман в стихах', 1833, '978-5-17-000004-4', 'А. С. Пушкин'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', '1984', 'Антиутопия', 1949, '978-5-17-000005-5', 'Дж. Оруэлл'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee6', 'Маленький принц', 'Повесть-сказка', 1943, '978-5-17-000006-6', 'А. де Сент-Экзюпери'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee7', 'Анна Каренина', 'Социально-психологический роман', 1877, '978-5-17-000007-7', 'Л. Н. Толстой'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee8', 'Гарри Поттер и философский камень', 'Роман о юном волшебнике', 1997, '978-5-17-000008-8', 'Дж. К. Роулинг'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee9', 'Шерлок Холмс. Этюд в багровых тонах', 'Детектив', 1887, '978-5-17-000009-9', 'А. Конан Дойл'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee10', 'Тихий Дон', 'Роман-эпопея', 1940, '978-5-17-000010-0', 'М. А. Шолохов'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee11', 'Собачье сердце', 'Повесть', 1925, '978-5-17-000011-1', 'М. А. Булгаков'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee12', 'Капитанская дочка', 'Исторический роман', 1836, '978-5-17-000012-2', 'А. С. Пушкин');

INSERT INTO book_genre (book_id, genre_id) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'dddddddd-dddd-dddd-dddd-dddddddddd07'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'dddddddd-dddd-dddd-dddd-dddddddddd05'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'dddddddd-dddd-dddd-dddd-dddddddddd04'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee6', 'dddddddd-dddd-dddd-dddd-dddddddddd08'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee7', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee7', 'dddddddd-dddd-dddd-dddd-dddddddddd07'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee8', 'dddddddd-dddd-dddd-dddd-dddddddddd04'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee9', 'dddddddd-dddd-dddd-dddd-dddddddddd03'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee10', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee10', 'dddddddd-dddd-dddd-dddd-dddddddddd06'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee11', 'dddddddd-dddd-dddd-dddd-dddddddddd01'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee12', 'dddddddd-dddd-dddd-dddd-dddddddddd06');

INSERT INTO book_copy (id, book_id, inventory_number, status) VALUES
('ffffffff-ffff-ffff-ffff-ffffffffff04', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'INV-004', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff05', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee3', 'INV-005', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff06', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee4', 'INV-006', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff07', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'INV-007', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff08', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee5', 'INV-008', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff09', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee6', 'INV-009', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff10', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee7', 'INV-010', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff11', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee8', 'INV-011', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff12', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee8', 'INV-012', 'ON_LOAN'),
('ffffffff-ffff-ffff-ffff-ffffffffff13', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee9', 'INV-013', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff14', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeee10', 'INV-014', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff15', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeee11', 'INV-015', 'AVAILABLE'),
('ffffffff-ffff-ffff-ffff-ffffffffff16', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeee12', 'INV-016', 'AVAILABLE');

INSERT INTO event (id, title, type, description, starts_at, capacity) VALUES
('33333333-3333-3333-3333-333333333303', 'Поэтический вечер', 'READING', 'Чтения современных авторов',
 CAST((CURRENT_DATE + 10) AS TIMESTAMP), 40),
('33333333-3333-3333-3333-333333333304', 'Мастер-класс по каллиграфии', 'WORKSHOP', 'Основы красивого письма',
 CAST((CURRENT_DATE + 21) AS TIMESTAMP), 15),
('33333333-3333-3333-3333-333333333305', 'Детский час сказок', 'CHILDREN', 'Для читателей 5–10 лет',
 CAST((CURRENT_DATE + 5) AS TIMESTAMP), 25),
('33333333-3333-3333-3333-333333333306', 'Лекция: русская классика XIX века', 'LECTURE', 'Обзор ключевых произведений',
 CAST((CURRENT_DATE + 30) AS TIMESTAMP), 50),
('33333333-3333-3333-3333-333333333307', 'Книжная ярмарка', 'FAIR', 'Новинки и редкие издания',
 CAST((CURRENT_DATE + 45) AS TIMESTAMP), 100);