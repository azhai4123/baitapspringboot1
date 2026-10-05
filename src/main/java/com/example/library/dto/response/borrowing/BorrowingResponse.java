package com.example.library.dto.response.borrowing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BorrowingResponse(Long id, Long memberId, String memberName, String memberEmail,
                                LocalDate borrowDate, LocalDate dueDate, LocalDate returnedDate,
                                String status, BigDecimal totalFine,
                                List<BorrowingDetailResponse> details) {
}
