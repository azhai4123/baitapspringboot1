package com.example.library.exception;

public class MemberNotFoundException extends ResourceNotFoundException {
    public MemberNotFoundException(Long id) { super("Không tìm thấy thành viên #" + id + "."); }
}
