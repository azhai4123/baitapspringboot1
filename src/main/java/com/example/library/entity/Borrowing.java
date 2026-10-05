package com.example.library.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrowing")
public class Borrowing {
    public static final String BORROWED = "BORROWED";
    public static final String PARTIALLY_RETURNED = "PARTIALLY_RETURNED";
    public static final String RETURNED = "RETURNED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Column(nullable = false, length = 30)
    private String status = BORROWED;

    @Column(name = "total_fine", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFine = BigDecimal.ZERO;

    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowingDetail> details = new ArrayList<>();

    protected Borrowing() {
    }

    public Borrowing(Member member, LocalDate borrowDate, LocalDate dueDate) {
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public void addDetail(BorrowingDetail detail) {
        details.add(detail);
        detail.setBorrowing(this);
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalFine() { return totalFine; }
    public void setTotalFine(BigDecimal totalFine) { this.totalFine = totalFine; }
    public List<BorrowingDetail> getDetails() { return details; }
}
