package com.example.library.dto.request.report;

import java.time.LocalDate;

public record OverdueReportRequest(LocalDate asOfDate) {
}
