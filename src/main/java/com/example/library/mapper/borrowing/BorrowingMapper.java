package com.example.library.mapper.borrowing;

import com.example.library.dto.response.borrowing.BorrowingDetailResponse;
import com.example.library.dto.response.borrowing.BorrowingResponse;
import com.example.library.entity.Borrowing;
import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {
    public BorrowingResponse toResponse(Borrowing borrowing) {
        var details = borrowing.getDetails().stream()
                .map(detail -> new BorrowingDetailResponse(detail.getId(), detail.getBook().getId(),
                        detail.getBook().getIsbn(), detail.getBook().getTitle(),
                        detail.getQuantity(), detail.getReturnedQuantity(), detail.getFineAmount()))
                .toList();
        return new BorrowingResponse(borrowing.getId(), borrowing.getMember().getId(),
                borrowing.getMember().getFullName(), borrowing.getMember().getEmail(),
                borrowing.getBorrowDate(), borrowing.getDueDate(), borrowing.getReturnedDate(),
                borrowing.getStatus(), borrowing.getTotalFine(), details);
    }
}
