INSERT INTO loan (id, card_id, copy_id, loan_date, due_date, returned_at, renewal_count, status) VALUES
('11111111-1111-1111-1111-111111111102', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'ffffffff-ffff-ffff-ffff-ffffffffff03',
 CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '28 days', 1, 'RETURNED');
