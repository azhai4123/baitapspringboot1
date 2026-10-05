package com.example.library.dto.request.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookCreateRequest {
    @NotBlank(message = "ISBN không được để trống.")
    @Size(max = 20, message = "ISBN tối đa 20 ký tự.")
    @Pattern(regexp = "[0-9Xx-]+", message = "ISBN chỉ được chứa số, dấu gạch ngang hoặc X.")
    private String isbn;
    @NotBlank(message = "Tên sách không được để trống.")
    @Size(max = 255, message = "Tên sách tối đa 255 ký tự.")
    private String title;
    @NotBlank(message = "Tác giả không được để trống.")
    @Size(max = 150, message = "Tên tác giả tối đa 150 ký tự.")
    private String author;
    @Min(value = 0, message = "Số lượng không được âm.")
    private int totalQuantity;
    @NotNull(message = "Vui lòng chọn thể loại.")
    private Long categoryId;

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
