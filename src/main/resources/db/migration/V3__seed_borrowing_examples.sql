INSERT INTO borrowing (member_id, borrow_date, due_date, returned_date, status, total_fine)
SELECT m.id, DATE_SUB(CURRENT_DATE, INTERVAL seed.days_ago DAY),
       DATE_SUB(CURRENT_DATE, INTERVAL seed.days_ago - 14 DAY),
       CASE WHEN seed.status = 'RETURNED' THEN DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY) ELSE NULL END,
       seed.status, seed.fine
FROM (
    SELECT 'minhanh@example.com' email, 45 days_ago, 'BORROWED' status, 0 fine
    UNION ALL SELECT 'hoangnam@example.com', 35, 'BORROWED', 0
    UNION ALL SELECT 'thuha@example.com', 25, 'BORROWED', 0
    UNION ALL SELECT 'quanghuy@example.com', 20, 'PARTIALLY_RETURNED', 25000
    UNION ALL SELECT 'ngoclan@example.com', 10, 'RETURNED', 0
) seed
JOIN member m ON m.email = seed.email
WHERE NOT EXISTS (
    SELECT 1 FROM borrowing existing
    WHERE existing.member_id = m.id AND existing.borrow_date = DATE_SUB(CURRENT_DATE, INTERVAL seed.days_ago DAY)
);

INSERT INTO borrowing_detail (borrowing_id, book_id, quantity, returned_quantity, fine_amount, last_returned_at)
SELECT b.id, bk.id, 1,
       CASE WHEN b.status = 'RETURNED' THEN 1 ELSE 0 END,
       CASE WHEN b.status = 'PARTIALLY_RETURNED' THEN 25000 ELSE 0 END,
       CASE WHEN b.status = 'RETURNED' THEN b.returned_date ELSE NULL END
FROM borrowing b
JOIN member m ON m.id = b.member_id
JOIN book bk ON bk.isbn = CASE m.email
    WHEN 'minhanh@example.com' THEN '9780000000001'
    WHEN 'hoangnam@example.com' THEN '9780000000006'
    WHEN 'thuha@example.com' THEN '9780000000011'
    WHEN 'quanghuy@example.com' THEN '9780000000014'
    WHEN 'ngoclan@example.com' THEN '9780000000017'
END
WHERE b.borrow_date IN (DATE_SUB(CURRENT_DATE, INTERVAL 45 DAY),
                        DATE_SUB(CURRENT_DATE, INTERVAL 35 DAY),
                        DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY),
                        DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY),
                        DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY))
  AND m.email IN ('minhanh@example.com', 'hoangnam@example.com', 'thuha@example.com',
                  'quanghuy@example.com', 'ngoclan@example.com')
  AND NOT EXISTS (SELECT 1 FROM borrowing_detail d WHERE d.borrowing_id = b.id);

UPDATE book bk
JOIN borrowing_detail d ON d.book_id = bk.id
JOIN borrowing b ON b.id = d.borrowing_id
SET bk.available_quantity = bk.available_quantity - (d.quantity - d.returned_quantity)
WHERE b.status IN ('BORROWED', 'PARTIALLY_RETURNED')
  AND d.last_returned_at IS NULL
  AND bk.available_quantity >= (d.quantity - d.returned_quantity);
