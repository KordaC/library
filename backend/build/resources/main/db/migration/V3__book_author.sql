ALTER TABLE book ADD COLUMN author_name VARCHAR(200);

UPDATE book SET author_name = 'Л. Н. Толстой' WHERE id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1';
UPDATE book SET author_name = 'М. А. Булгаков' WHERE id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2';
