CREATE TABLE IF NOT EXISTS category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS book (
    id BIGINT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(150) NOT NULL,
    total_quantity INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    category_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_book_isbn (isbn),
    KEY fk_book_category (category_id),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_book_inventory CHECK (total_quantity >= 0 AND available_quantity >= 0 AND available_quantity <= total_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS borrowing (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE,
    status VARCHAR(30) NOT NULL,
    total_fine DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_borrowing_member (member_id),
    KEY idx_borrowing_overdue (status, due_date),
    CONSTRAINT fk_borrowing_member FOREIGN KEY (member_id) REFERENCES member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS borrowing_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    borrowing_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    returned_quantity INT NOT NULL DEFAULT 0,
    fine_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    last_returned_at DATE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_borrowing_book (borrowing_id, book_id),
    KEY idx_borrowing_detail_book (book_id),
    CONSTRAINT fk_detail_borrowing FOREIGN KEY (borrowing_id) REFERENCES borrowing (id),
    CONSTRAINT fk_detail_book FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT chk_borrowing_quantities CHECK (quantity > 0 AND returned_quantity >= 0 AND returned_quantity <= quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
