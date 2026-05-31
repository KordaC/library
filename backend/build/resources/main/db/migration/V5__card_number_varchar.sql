-- Hibernate ожидает VARCHAR, Flyway V1 создал CHAR(5)
-- Синтаксис PostgreSQL (работает и в H2 с MODE=PostgreSQL)
ALTER TABLE library_card ALTER COLUMN card_number TYPE VARCHAR(5);
