package com.example.library.dto.request.borrowing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BorrowingReturnRequest {
    @NotNull(message = "Thiếu dòng chi tiết sách.")
    private Long detailId;
    @Min(value = 0, message = "Số lượng trả không được âm.")
    private int quantity;

    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
