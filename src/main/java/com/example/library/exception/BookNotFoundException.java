package com.example.library.exception;

public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(Long id) { super("Không tìm thấy sách #" + id + "."); }
}
