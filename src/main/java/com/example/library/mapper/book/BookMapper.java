package com.example.library.mapper.book;

import com.example.library.dto.response.book.BookResponse;
import com.example.library.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                book.getTotalQuantity(), book.getAvailableQuantity(), book.getStatus(),
                book.getCategory() == null ? null : book.getCategory().getId(),
                book.getCategory() == null ? "" : book.getCategory().getName());
    }
}
