package com.example.library.controller.report;

import com.example.library.service.report.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) { this.service = service; }

    @GetMapping("/overdue")
    public String overdue(Model model) {
        model.addAttribute("items", service.overdue());
        return "report/overdue";
    }

    @GetMapping("/popular-books")
    public String popularBooks(Model model) {
        model.addAttribute("items", service.mostBorrowed());
        return "report/popular-books";
    }

    @GetMapping("/members")
    public String memberBorrowings(Model model) {
        model.addAttribute("items", service.borrowingsByMember());
        return "report/members";
    }
}
