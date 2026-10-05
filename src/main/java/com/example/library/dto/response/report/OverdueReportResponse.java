package com.example.library.dto.response.report;

import java.time.LocalDate;

public record OverdueReportResponse(Long borrowingId, String memberName, String memberEmail,
                                    LocalDate borrowDate, LocalDate dueDate, long daysOverdue) {
}
