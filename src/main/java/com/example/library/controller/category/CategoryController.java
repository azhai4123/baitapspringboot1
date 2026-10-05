package com.example.library.controller.category;

import com.example.library.dto.request.category.CategoryCreateRequest;
import com.example.library.dto.request.category.CategoryUpdateRequest;
import com.example.library.service.category.CategoryService;
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
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        var result = service.search(q, true, PageRequest.of(Math.max(page, 0), boundedSize(size), Sort.by("id").descending()));
        model.addAttribute("categories", result);
        model.addAttribute("q", q);
        return "category/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CategoryCreateRequest());
        model.addAttribute("pageTitle", "Thêm thể loại");
        model.addAttribute("action", "/categories");
        return "category/form";
    }

    @PostMapping
    public String create(@Valid @org.springframework.web.bind.annotation.ModelAttribute("form") CategoryCreateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm thể loại");
            model.addAttribute("action", "/categories");
            return "category/form";
        }
        service.create(form);
        redirect.addFlashAttribute("success", "Đã tạo thể loại.");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var category = service.get(id);
        var form = new CategoryUpdateRequest();
        form.setName(category.name());
        form.setDescription(category.description());
        model.addAttribute("form", form);
        model.addAttribute("pageTitle", "Cập nhật thể loại");
        model.addAttribute("action", "/categories/" + id);
        return "category/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @org.springframework.web.bind.annotation.ModelAttribute("form") CategoryUpdateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật thể loại");
            model.addAttribute("action", "/categories/" + id);
            return "category/form";
        }
        service.update(id, form);
        redirect.addFlashAttribute("success", "Đã cập nhật thể loại.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirect) {
        service.deactivate(id);
        redirect.addFlashAttribute("success", "Đã ngừng hoạt động thể loại.");
        return "redirect:/categories";
    }

    private static int boundedSize(int size) { return Math.max(1, Math.min(size, 100)); }
}
