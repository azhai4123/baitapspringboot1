# Library Management

Spring Boot MVC library borrowing application using Thymeleaf, Spring Data JPA, MySQL and Flyway.

## Requirements

- Java 17+
- MySQL 8+

The default local connection is `jdbc:mysql://localhost:3306/library_db`, user `root`, empty password. Override it with `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables. Create the database once if it does not exist:

```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

On startup Flyway applies migrations. Existing `category`, `book` and `member` tables are not dropped; the migrations create missing tables and add sample data only when matching rows are absent. Back up a database before applying schema migrations in a shared or production environment.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`. Run tests with:

```powershell
.\mvnw.cmd test
```

Tests use an isolated H2 database in MySQL compatibility mode. Runtime uses the configured MySQL database.

## Main routes

| Route | Purpose |
|---|---|
| `/books` | Search, page, create, update, and soft-delete books |
| `/categories` | Manage and deactivate categories |
| `/members` | Manage, block, and unblock members |
| `/borrowings` | Create borrowing receipts and browse history |
| `/borrowings/{id}` | View receipt details |
| `/borrowings/{id}/return` | Record partial or full returns |
| `/reports/overdue` | Overdue open receipts |
| `/reports/popular-books` | Five most-borrowed books |
| `/reports/members` | Borrowing count per member |

Borrowing and return use transactions; book inventory is locked while a borrowing is processed. Late-return fines are 5,000 VND per overdue day per returned copy.
