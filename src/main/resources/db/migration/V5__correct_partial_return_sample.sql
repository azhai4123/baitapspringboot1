UPDATE borrowing_detail d
JOIN borrowing b ON b.id = d.borrowing_id
JOIN member m ON m.id = b.member_id
SET d.quantity = 2,
    d.returned_quantity = 1,
    d.fine_amount = 25000,
    d.last_returned_at = DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)
WHERE m.email = 'quanghuy@example.com'
  AND b.status = 'PARTIALLY_RETURNED'
  AND d.quantity = 1
  AND d.returned_quantity = 0;
