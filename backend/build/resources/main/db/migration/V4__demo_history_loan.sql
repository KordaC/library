INSERT INTO loan (id, card_id, copy_id, loan_date, due_date, returned_at, renewal_count, status) VALUES
('11111111-1111-1111-1111-111111111102', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'ffffffff-ffff-ffff-ffff-ffffffffff03',
 CURRENT_DATE - 60, CURRENT_DATE - 30, CURRENT_DATE - 28, 1, 'RETURNED');
