package com.example.library.mapper.report;

import com.example.library.dto.response.report.OverdueReportResponse;
import com.example.library.entity.Borrowing;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class OverdueReportMapper {
    public OverdueReportResponse toResponse(Borrowing borrowing, LocalDate asOfDate) {
        return new OverdueReportResponse(borrowing.getId(), borrowing.getMember().getFullName(),
                borrowing.getMember().getEmail(), borrowing.getBorrowDate(), borrowing.getDueDate(),
                ChronoUnit.DAYS.between(borrowing.getDueDate(), asOfDate));
    }
}
