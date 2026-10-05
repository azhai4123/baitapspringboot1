package com.example.library;

import com.example.library.dto.request.book.BookCreateRequest;
import com.example.library.dto.request.borrowing.BorrowingCreateRequest;
import com.example.library.dto.request.borrowing.BorrowingDetailRequest;
import com.example.library.dto.request.borrowing.BorrowingReturnRequest;
import com.example.library.dto.request.category.CategoryCreateRequest;
import com.example.library.dto.request.member.MemberCreateRequest;
import com.example.library.entity.Borrowing;
import com.example.library.service.book.BookService;
import com.example.library.service.borrowing.BorrowingService;
import com.example.library.service.category.CategoryService;
import com.example.library.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LibraryManagementApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BookService bookService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BorrowingService borrowingService;

    @Test
    void contextLoads() {
    }

    @Test
    void mainScreensRender() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/books"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/books/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/categories"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/categories/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/members"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/members/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/borrowings"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/borrowings/new"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/reports/overdue"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/reports/popular-books"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/reports/members"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }

    @Test
    void borrowingAndPartialReturnUpdateInventory() {
        String token = Long.toString(System.nanoTime());
        CategoryCreateRequest category = new CategoryCreateRequest();
        category.setName("Test category " + token);
        Long categoryId = categoryService.create(category).id();

        BookCreateRequest book = new BookCreateRequest();
        book.setIsbn(token);
        book.setTitle("Test book " + token);
        book.setAuthor("Test author");
        book.setTotalQuantity(2);
        book.setCategoryId(categoryId);
        Long bookId = bookService.create(book).id();

        MemberCreateRequest member = new MemberCreateRequest();
        member.setFullName("Test member");
        member.setEmail(token + "@example.com");
        Long memberId = memberService.create(member).id();

        BorrowingDetailRequest detail = new BorrowingDetailRequest();
        detail.setBookId(bookId);
        detail.setQuantity(2);
        BorrowingCreateRequest request = new BorrowingCreateRequest();
        request.setMemberId(memberId);
        request.setDetails(java.util.List.of(detail));
        var borrowing = borrowingService.create(request);

        org.junit.jupiter.api.Assertions.assertEquals(0, bookService.get(bookId).availableQuantity());

        BorrowingReturnRequest firstReturn = new BorrowingReturnRequest();
        firstReturn.setDetailId(borrowing.details().get(0).id());
        firstReturn.setQuantity(1);
        var partiallyReturned = borrowingService.returnBooks(borrowing.id(), java.util.List.of(firstReturn));
        org.junit.jupiter.api.Assertions.assertEquals(Borrowing.PARTIALLY_RETURNED, partiallyReturned.status());
        org.junit.jupiter.api.Assertions.assertEquals(1, bookService.get(bookId).availableQuantity());

        var secondReturn = borrowingService.returnBooks(borrowing.id(), java.util.List.of(firstReturn));
        org.junit.jupiter.api.Assertions.assertEquals(Borrowing.RETURNED, secondReturn.status());
        org.junit.jupiter.api.Assertions.assertEquals(2, bookService.get(bookId).availableQuantity());
    }
}
