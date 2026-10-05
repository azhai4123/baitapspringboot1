package com.example.library.exception;

public class MemberAlreadyExistsException extends BusinessException {
    public MemberAlreadyExistsException(String email) { super("Email \"" + email + "\" đã được sử dụng."); }
}
