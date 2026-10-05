package com.example.library.repository;

import com.example.library.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbnIgnoreCase(String isbn);
    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, Long id);

    @Query("select b from Book b left join b.category c where b.status <> 'DELETED' "
            + "and (:query = '' or lower(b.title) like lower(concat('%', :query, '%')) "
            + "or lower(b.author) like lower(concat('%', :query, '%')) "
            + "or lower(b.isbn) like lower(concat('%', :query, '%')) "
            + "or lower(c.name) like lower(concat('%', :query, '%'))) "
            + "and (:categoryId is null or c.id = :categoryId)")
    Page<Book> search(@Param("query") String query, @Param("categoryId") Long categoryId,
                      Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    boolean existsByCategoryIdAndStatusNot(Long categoryId, String status);
}
