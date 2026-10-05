package com.example.library.repository;

import com.example.library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("select c from Category c where (:includeInactive = true or c.active = true) "
            + "and (:query = '' or lower(c.name) like lower(concat('%', :query, '%')))")
    Page<Category> search(@Param("query") String query,
                          @Param("includeInactive") boolean includeInactive,
                          Pageable pageable);
}
