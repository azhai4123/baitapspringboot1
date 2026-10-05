package com.example.library.controller.borrowing;

import com.example.library.dto.request.borrowing.BorrowingCreateRequest;
import com.example.library.dto.request.borrowing.BorrowingDetailRequest;
import com.example.library.dto.request.borrowing.BorrowingReturnRequest;
import com.example.library.exception.BorrowingNotFoundException;
import com.example.library.service.book.BookService;
import com.example.library.service.borrowing.BorrowingService;
import com.example.library.service.member.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {
    private final BorrowingService service;
    private final MemberService memberService;
    private final BookService bookService;

    public BorrowingController(BorrowingService service, MemberService memberService,
                               BookService bookService) {
        this.service = service;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size, Model model) {
        model.addAttribute("borrowings", service.list(PageRequest.of(Math.max(0, page),
                boundedSize(size), Sort.by("borrowDate").descending())));
        return "borrowing/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        var form = new BorrowingCreateRequest();
        form.getDetails().add(new BorrowingDetailRequest());
        model.addAttribute("form", form);
        addBorrowingChoices(model);
        return "borrowing/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") BorrowingCreateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            addBorrowingChoices(model);
            return "borrowing/form";
        }
        var created = service.create(form);
        redirect.addFlashAttribute("success", "Đã tạo phiếu mượn #" + created.id() + ".");
        return "redirect:/borrowings/" + created.id();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("borrowing", service.get(id));
        return "borrowing/details";
    }

    @GetMapping("/{id}/return")
    public String returnForm(@PathVariable Long id, Model model) {
        var borrowing = service.get(id);
        var form = new ReturnForm();
        for (var detail : borrowing.details()) {
            if (detail.quantity() > detail.returnedQuantity()) {
                var line = new BorrowingReturnRequest();
                line.setDetailId(detail.id());
                form.getDetails().add(line);
            }
        }
        if (form.getDetails().isEmpty()) throw new BorrowingNotFoundException(id);
        model.addAttribute("borrowing", borrowing);
        model.addAttribute("form", form);
        return "borrowing/return";
    }

    @PostMapping("/{id}/return")
    public String returnBooks(@PathVariable Long id,
                              @Valid @ModelAttribute("form") ReturnForm form,
                              BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("borrowing", service.get(id));
            return "borrowing/return";
        }
        service.returnBooks(id, form.getDetails());
        redirect.addFlashAttribute("success", "Đã ghi nhận trả sách.");
        return "redirect:/borrowings/" + id;
    }

    private void addBorrowingChoices(Model model) {
        model.addAttribute("members", memberService.search("", "ACTIVE",
                PageRequest.of(0, 100, Sort.by("fullName"))).getContent());
        model.addAttribute("books", bookService.search("", null,
                PageRequest.of(0, 500, Sort.by("title"))).getContent());
    }

    private static int boundedSize(int size) { return Math.max(1, Math.min(size, 100)); }

    public static class ReturnForm {
        @Valid
        private java.util.List<BorrowingReturnRequest> details = new ArrayList<>();
        public java.util.List<BorrowingReturnRequest> getDetails() { return details; }
        public void setDetails(java.util.List<BorrowingReturnRequest> details) { this.details = details; }
    }
}
