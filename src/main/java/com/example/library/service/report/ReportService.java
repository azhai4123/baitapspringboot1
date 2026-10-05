package com.example.library.service.report;

import com.example.library.dto.response.report.OverdueReportResponse;
import com.example.library.dto.response.report.PopularBookResponse;
import com.example.library.mapper.report.OverdueReportMapper;
import com.example.library.repository.BorrowingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final BorrowingRepository borrowingRepository;
    private final OverdueReportMapper overdueMapper;

    public ReportService(BorrowingRepository borrowingRepository, OverdueReportMapper overdueMapper) {
        this.borrowingRepository = borrowingRepository;
        this.overdueMapper = overdueMapper;
    }

    @Transactional(readOnly = true)
    public List<OverdueReportResponse> overdue() {
        LocalDate today = LocalDate.now();
        return borrowingRepository.findOverdue(today).stream()
                .map(borrowing -> overdueMapper.toResponse(borrowing, today)).toList();
    }

    @Transactional(readOnly = true)
    public List<PopularBookResponse> mostBorrowed() {
        return borrowingRepository.findMostBorrowed(PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<BorrowingRepository.MemberBorrowCount> borrowingsByMember() {
        return borrowingRepository.countBorrowingsByMember();
    }
}
