INSERT INTO category (name, description, active)
SELECT seed.name, seed.description, 1
FROM (
    SELECT 'Văn học' AS name, 'Tiểu thuyết, truyện ngắn và tác phẩm văn học.' AS description
    UNION ALL SELECT 'Khoa học', 'Sách khoa học và công nghệ.'
    UNION ALL SELECT 'Lịch sử', 'Sách lịch sử Việt Nam và thế giới.'
    UNION ALL SELECT 'Kinh tế', 'Kinh doanh, quản trị và kinh tế học.'
    UNION ALL SELECT 'Thiếu nhi', 'Sách dành cho thiếu nhi.'
) seed
WHERE NOT EXISTS (SELECT 1 FROM category existing WHERE existing.name = seed.name);

INSERT INTO book (isbn, title, author, total_quantity, available_quantity, status, category_id)
SELECT seed.isbn, seed.title, seed.author, 5, 5, 'AVAILABLE', c.id
FROM (
    SELECT '9780000000001' isbn, 'Dế mèn phiêu lưu ký' title, 'Tô Hoài' author, 'Văn học' category_name
    UNION ALL SELECT '9780000000002', 'Tắt đèn', 'Ngô Tất Tố', 'Văn học'
    UNION ALL SELECT '9780000000003', 'Số đỏ', 'Vũ Trọng Phụng', 'Văn học'
    UNION ALL SELECT '9780000000004', 'Tuổi thơ dữ dội', 'Phùng Quán', 'Văn học'
    UNION ALL SELECT '9780000000005', 'Cho tôi xin một vé đi tuổi thơ', 'Nguyễn Nhật Ánh', 'Văn học'
    UNION ALL SELECT '9780000000006', 'Lược sử thời gian', 'Stephen Hawking', 'Khoa học'
    UNION ALL SELECT '9780000000007', 'Vũ trụ trong vỏ hạt dẻ', 'Stephen Hawking', 'Khoa học'
    UNION ALL SELECT '9780000000008', 'Nguồn gốc các loài', 'Charles Darwin', 'Khoa học'
    UNION ALL SELECT '9780000000009', 'Sapiens', 'Yuval Noah Harari', 'Lịch sử'
    UNION ALL SELECT '9780000000010', 'Lược sử loài người', 'Yuval Noah Harari', 'Lịch sử'
    UNION ALL SELECT '9780000000011', 'Đại Việt sử ký toàn thư', 'Ngô Sĩ Liên', 'Lịch sử'
    UNION ALL SELECT '9780000000012', 'Lịch sử thế giới', 'Nguyễn Hiến Lê', 'Lịch sử'
    UNION ALL SELECT '9780000000013', 'Nghĩ giàu làm giàu', 'Napoleon Hill', 'Kinh tế'
    UNION ALL SELECT '9780000000014', 'Đắc nhân tâm', 'Dale Carnegie', 'Kinh tế'
    UNION ALL SELECT '9780000000015', 'Nhà giả kim', 'Paulo Coelho', 'Văn học'
    UNION ALL SELECT '9780000000016', 'Không gia đình', 'Hector Malot', 'Văn học'
    UNION ALL SELECT '9780000000017', 'Hoàng tử bé', 'Antoine de Saint-Exupéry', 'Thiếu nhi'
    UNION ALL SELECT '9780000000018', 'Alice ở xứ sở diệu kỳ', 'Lewis Carroll', 'Thiếu nhi'
    UNION ALL SELECT '9780000000019', 'Những tấm lòng cao cả', 'Edmondo De Amicis', 'Thiếu nhi'
    UNION ALL SELECT '9780000000020', 'Kính vạn hoa', 'Nguyễn Nhật Ánh', 'Thiếu nhi'
) seed
JOIN category c ON c.name = seed.category_name
WHERE NOT EXISTS (SELECT 1 FROM book existing WHERE existing.isbn = seed.isbn);

INSERT INTO member (full_name, email, phone, status)
SELECT seed.full_name, seed.email, seed.phone, seed.status
FROM (
    SELECT 'Nguyễn Minh Anh' full_name, 'minhanh@example.com' email, '0900000001' phone, 'ACTIVE' status
    UNION ALL SELECT 'Trần Hoàng Nam', 'hoangnam@example.com', '0900000002', 'ACTIVE'
    UNION ALL SELECT 'Lê Thu Hà', 'thuha@example.com', '0900000003', 'ACTIVE'
    UNION ALL SELECT 'Phạm Quang Huy', 'quanghuy@example.com', '0900000004', 'ACTIVE'
    UNION ALL SELECT 'Võ Ngọc Lan', 'ngoclan@example.com', '0900000005', 'ACTIVE'
    UNION ALL SELECT 'Đặng Tuấn Kiệt', 'tuankiet@example.com', '0900000006', 'ACTIVE'
    UNION ALL SELECT 'Bùi Khánh Linh', 'khanhlinh@example.com', '0900000007', 'ACTIVE'
    UNION ALL SELECT 'Đỗ Gia Bảo', 'giabao@example.com', '0900000008', 'ACTIVE'
    UNION ALL SELECT 'Ngô Phương Thảo', 'phuongthao@example.com', '0900000009', 'BLOCKED'
    UNION ALL SELECT 'Hoàng Đức Long', 'duclong@example.com', '0900000010', 'ACTIVE'
) seed
WHERE NOT EXISTS (SELECT 1 FROM member existing WHERE existing.email = seed.email);
