package com.example.library.dto.response.borrowing;

import java.math.BigDecimal;

public record BorrowingDetailResponse(Long id, Long bookId, String isbn, String title,
                                      int quantity, int returnedQuantity,
                                      BigDecimal fineAmount) {
}
