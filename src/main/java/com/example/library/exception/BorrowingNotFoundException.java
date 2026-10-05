package com.example.library.exception;

public class BorrowingNotFoundException extends ResourceNotFoundException {
    public BorrowingNotFoundException(Long id) { super("Không tìm thấy phiếu mượn #" + id + "."); }
}
