package com.example.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String INACTIVE = "INACTIVE";
    public static final String DELETED = "DELETED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(nullable = false, length = 50)
    private String status = AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_book_category"))
    private Category category;

    protected Book() {
    }

    public Book(String isbn, String title, String author, int totalQuantity, Category category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.category = category;
        this.status = AVAILABLE;
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public void adjustInventory(int newTotal) {
        int borrowedQuantity = totalQuantity - availableQuantity;
        if (newTotal < borrowedQuantity) {
            throw new IllegalArgumentException("Tổng số lượng không được thấp hơn số sách đang được mượn.");
        }
        totalQuantity = newTotal;
        availableQuantity = newTotal - borrowedQuantity;
    }
}
