package com.example.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "borrowing_detail")
public class BorrowingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrowing_id", nullable = false)
    private Borrowing borrowing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "returned_quantity", nullable = false)
    private int returnedQuantity;

    @Column(name = "fine_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "last_returned_at")
    private LocalDate lastReturnedAt;

    protected BorrowingDetail() {
    }

    public BorrowingDetail(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    void setBorrowing(Borrowing borrowing) { this.borrowing = borrowing; }
    public Long getId() { return id; }
    public Borrowing getBorrowing() { return borrowing; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public int getReturnedQuantity() { return returnedQuantity; }
    public int getOutstandingQuantity() { return quantity - returnedQuantity; }
    public BigDecimal getFineAmount() { return fineAmount; }
    public LocalDate getLastReturnedAt() { return lastReturnedAt; }

    public void returnQuantity(int count, BigDecimal fine, LocalDate returnDate) {
        returnedQuantity += count;
        fineAmount = fineAmount.add(fine);
        lastReturnedAt = returnDate;
    }
}
