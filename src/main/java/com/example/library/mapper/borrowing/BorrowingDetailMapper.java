package com.example.library.mapper.borrowing;

import com.example.library.dto.response.borrowing.BorrowingDetailResponse;
import com.example.library.entity.BorrowingDetail;
import org.springframework.stereotype.Component;

@Component
public class BorrowingDetailMapper {
    public BorrowingDetailResponse toResponse(BorrowingDetail detail) {
        return new BorrowingDetailResponse(detail.getId(), detail.getBook().getId(),
                detail.getBook().getIsbn(), detail.getBook().getTitle(),
                detail.getQuantity(), detail.getReturnedQuantity(), detail.getFineAmount());
    }
}
