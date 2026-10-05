package com.example.library.controller.member;

import com.example.library.dto.request.member.MemberCreateRequest;
import com.example.library.dto.request.member.MemberUpdateRequest;
import com.example.library.service.member.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService service;

    public MemberController(MemberService service) { this.service = service; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size, Model model) {
        model.addAttribute("members", service.search(q, status,
                PageRequest.of(Math.max(page, 0), boundedSize(size), Sort.by("createdAt").descending())));
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        return "member/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new MemberCreateRequest());
        model.addAttribute("pageTitle", "Thêm thành viên");
        model.addAttribute("action", "/members");
        return "member/form";
    }

    @PostMapping
    public String create(@Valid @org.springframework.web.bind.annotation.ModelAttribute("form") MemberCreateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm thành viên");
            model.addAttribute("action", "/members");
            return "member/form";
        }
        service.create(form);
        redirect.addFlashAttribute("success", "Đã thêm thành viên.");
        return "redirect:/members";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var member = service.get(id);
        var form = new MemberUpdateRequest();
        form.setFullName(member.fullName());
        form.setEmail(member.email());
        form.setPhone(member.phone());
        model.addAttribute("form", form);
        model.addAttribute("pageTitle", "Cập nhật thành viên");
        model.addAttribute("action", "/members/" + id);
        return "member/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @org.springframework.web.bind.annotation.ModelAttribute("form") MemberUpdateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật thành viên");
            model.addAttribute("action", "/members/" + id);
            return "member/form";
        }
        service.update(id, form);
        redirect.addFlashAttribute("success", "Đã cập nhật thành viên.");
        return "redirect:/members";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam boolean blocked, RedirectAttributes redirect) {
        service.setBlocked(id, blocked);
        redirect.addFlashAttribute("success", blocked ? "Đã khóa thành viên." : "Đã mở khóa thành viên.");
        return "redirect:/members";
    }

    private static int boundedSize(int size) { return Math.max(1, Math.min(size, 100)); }
}
