package com.example.library.repository;

import com.example.library.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("select m from Member m where (:status = '' or m.status = :status) "
            + "and (:query = '' or lower(m.fullName) like lower(concat('%', :query, '%')) "
            + "or lower(m.email) like lower(concat('%', :query, '%')))")
    Page<Member> search(@Param("query") String query, @Param("status") String status,
                        Pageable pageable);
}
