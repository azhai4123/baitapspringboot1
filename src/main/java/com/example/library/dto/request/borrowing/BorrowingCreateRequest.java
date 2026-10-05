package com.example.library.dto.request.borrowing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class BorrowingCreateRequest {
    @NotNull(message = "Vui lòng chọn thành viên.")
    private Long memberId;
    @Valid
    @Size(min = 1, message = "Phiếu mượn phải có ít nhất một cuốn sách.")
    private List<BorrowingDetailRequest> details = new ArrayList<>();

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public List<BorrowingDetailRequest> getDetails() { return details; }
    public void setDetails(List<BorrowingDetailRequest> details) { this.details = details; }
}
