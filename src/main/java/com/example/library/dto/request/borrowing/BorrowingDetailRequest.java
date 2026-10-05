package com.example.library.dto.request.borrowing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BorrowingDetailRequest {
    @NotNull(message = "Vui lòng chọn sách.")
    private Long bookId;
    @Min(value = 1, message = "Số lượng mượn tối thiểu là 1.")
    private int quantity = 1;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
