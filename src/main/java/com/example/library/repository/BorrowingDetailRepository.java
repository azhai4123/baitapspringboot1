package com.example.library.repository;

import com.example.library.entity.BorrowingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, Long> {
    @Query("select case when count(d) > 0 then true else false end from BorrowingDetail d "
            + "where d.book.id = :bookId and d.returnedQuantity < d.quantity")
    boolean hasOutstandingLoans(@Param("bookId") Long bookId);
}
