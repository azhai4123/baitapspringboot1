package com.example.library.exception;

public class CategoryAlreadyExistsException extends BusinessException {
    public CategoryAlreadyExistsException(String name) { super("Thể loại \"" + name + "\" đã tồn tại."); }
}
