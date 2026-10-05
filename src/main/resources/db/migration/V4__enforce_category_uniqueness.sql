UPDATE category SET active = 1 WHERE active IS NULL;
UPDATE book SET status = 'AVAILABLE' WHERE status IS NULL;
UPDATE member SET status = 'ACTIVE' WHERE status IS NULL;

SET @category_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'category'
      AND index_name = 'uk_category_name'
);
SET @category_index_sql = IF(
    @category_index_exists = 0,
    'CREATE UNIQUE INDEX uk_category_name ON category (name)',
    'SELECT 1'
);
PREPARE category_index_statement FROM @category_index_sql;
EXECUTE category_index_statement;
DEALLOCATE PREPARE category_index_statement;
