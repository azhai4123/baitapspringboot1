package com.example.library.controller.book;

import com.example.library.dto.request.book.BookCreateRequest;
import com.example.library.dto.request.book.BookUpdateRequest;
import com.example.library.service.book.BookService;
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
@RequestMapping("/books")
public class BookController {
    private final BookService service;
    private final CategoryService categoryService;

    public BookController(BookService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size, Model model) {
        model.addAttribute("books", service.search(q, categoryId,
                PageRequest.of(Math.max(page, 0), boundedSize(size), Sort.by("title").ascending())));
        model.addAttribute("categories", categoryService.search("", false, PageRequest.of(0, 100, Sort.by("name"))));
        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        return "book/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookCreateRequest());
        model.addAttribute("pageTitle", "Thêm sách");
        model.addAttribute("action", "/books");
        addCategories(model);
        return "book/form";
    }

    @PostMapping
    public String create(@Valid @org.springframework.web.bind.annotation.ModelAttribute("form") BookCreateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm sách");
            model.addAttribute("action", "/books");
            addCategories(model);
            return "book/form";
        }
        service.create(form);
        redirect.addFlashAttribute("success", "Đã thêm sách.");
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var book = service.get(id);
        var form = new BookUpdateRequest();
        form.setIsbn(book.isbn());
        form.setTitle(book.title());
        form.setAuthor(book.author());
        form.setTotalQuantity(book.totalQuantity());
        form.setCategoryId(book.categoryId());
        model.addAttribute("form", form);
        model.addAttribute("pageTitle", "Cập nhật sách");
        model.addAttribute("action", "/books/" + id);
        addCategories(model);
        return "book/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @org.springframework.web.bind.annotation.ModelAttribute("form") BookUpdateRequest form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật sách");
            model.addAttribute("action", "/books/" + id);
            addCategories(model);
            return "book/form";
        }
        service.update(id, form);
        redirect.addFlashAttribute("success", "Đã cập nhật sách.");
        return "redirect:/books";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirect) {
        service.deactivate(id);
        redirect.addFlashAttribute("success", "Đã ngừng hoạt động sách.");
        return "redirect:/books";
    }

    private void addCategories(Model model) {
        model.addAttribute("categories",
                categoryService.search("", false, PageRequest.of(0, 100, Sort.by("name"))).getContent());
    }

    private static int boundedSize(int size) { return Math.max(1, Math.min(size, 100)); }
}
