package com.example.library.dto.response.book;

public record BookResponse(Long id, String isbn, String title, String author,
                           int totalQuantity, int availableQuantity, String status,
                           Long categoryId, String categoryName) {
}
