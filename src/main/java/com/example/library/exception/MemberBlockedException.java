package com.example.library.exception;

public class MemberBlockedException extends BusinessException {
    public MemberBlockedException() { super("Thành viên đang bị khóa, không thể tạo phiếu mượn."); }
}
