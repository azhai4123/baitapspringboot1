package com.example.library.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(Long id) { super("Không tìm thấy thể loại #" + id + "."); }
}
