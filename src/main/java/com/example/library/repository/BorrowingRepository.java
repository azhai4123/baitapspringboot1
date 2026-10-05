package com.example.library.repository;

import com.example.library.entity.Borrowing;
import com.example.library.dto.response.report.PopularBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Page<Borrowing> findAllByOrderByBorrowDateDescIdDesc(Pageable pageable);

    @Query("select b from Borrowing b join fetch b.member where b.status <> 'RETURNED' "
            + "and b.dueDate < :date order by b.dueDate asc")
    List<Borrowing> findOverdue(@Param("date") LocalDate date);

    @Query("select new com.example.library.dto.response.report.PopularBookResponse("
            + "d.book.id, d.book.title, d.book.author, sum(d.quantity)) "
            + "from BorrowingDetail d group by d.book.id, d.book.title, d.book.author "
            + "order by sum(d.quantity) desc")
    List<PopularBookResponse> findMostBorrowed(Pageable pageable);

    @Query("select m.id as memberId, m.fullName as memberName, count(b.id) as borrowingCount "
            + "from Member m left join Borrowing b on b.member = m "
            + "group by m.id, m.fullName order by count(b.id) desc")
    List<MemberBorrowCount> countBorrowingsByMember();

    interface MemberBorrowCount {
        Long getMemberId();
        String getMemberName();
        Long getBorrowingCount();
    }
}
