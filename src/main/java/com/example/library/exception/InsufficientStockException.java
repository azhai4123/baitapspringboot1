package com.example.library.exception;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String title) { super("Sách \"" + title + "\" không đủ số lượng còn lại."); }
}
