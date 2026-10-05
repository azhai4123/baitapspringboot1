package com.example.library.exception;

public class BookAlreadyExistsException extends BusinessException {
    public BookAlreadyExistsException(String isbn) { super("ISBN \"" + isbn + "\" đã tồn tại."); }
}
