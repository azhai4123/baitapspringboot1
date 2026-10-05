package com.example.library.dto.response.report;

public record PopularBookResponse(Long bookId, String title, String author, long borrowCount) {
}
