package com.example.library.service.borrowing;

import com.example.library.dto.request.borrowing.BorrowingCreateRequest;
import com.example.library.dto.request.borrowing.BorrowingDetailRequest;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.BorrowingDetail;
import com.example.library.entity.Member;
import com.example.library.exception.MemberBlockedException;
import com.example.library.mapper.borrowing.BorrowingMapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingDetailRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {
    @Mock BorrowingRepository borrowingRepository;
    @Mock BorrowingDetailRepository detailRepository;
    @Mock BookRepository bookRepository;
    @Mock MemberRepository memberRepository;
    private BorrowingService service;

    @BeforeEach
    void setUp() {
        service = new BorrowingService(borrowingRepository, detailRepository, bookRepository,
                memberRepository, new BorrowingMapper());
    }

    @Test
    void createsBorrowingAndDecreasesInventory() {
        Member member = org.mockito.Mockito.mock(Member.class);
        when(member.getStatus()).thenReturn(Member.ACTIVE);
        when(member.getId()).thenReturn(4L);
        when(member.getFullName()).thenReturn("Test Member");
        when(member.getEmail()).thenReturn("member@example.com");
        when(memberRepository.findById(4L)).thenReturn(Optional.of(member));

        Book book = org.mockito.Mockito.mock(Book.class);
        when(book.getId()).thenReturn(8L);
        when(book.getStatus()).thenReturn(Book.AVAILABLE);
        when(book.getTitle()).thenReturn("Test book");
        when(book.getAvailableQuantity()).thenReturn(3);
        when(bookRepository.findByIdForUpdate(8L)).thenReturn(Optional.of(book));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowingDetailRequest detail = new BorrowingDetailRequest();
        detail.setBookId(8L);
        detail.setQuantity(2);
        BorrowingCreateRequest request = new BorrowingCreateRequest();
        request.setMemberId(4L);
        request.setDetails(List.of(detail));

        var response = service.create(request);

        assertEquals(1, response.details().size());
        assertEquals(2, response.details().get(0).quantity());
        assertEquals(Borrowing.BORROWED, response.status());
        verify(book).setAvailableQuantity(1);
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    void rejectsBlockedMemberBeforeChangingStock() {
        Member member = org.mockito.Mockito.mock(Member.class);
        when(member.getStatus()).thenReturn(Member.BLOCKED);
        when(memberRepository.findById(4L)).thenReturn(Optional.of(member));
        BorrowingCreateRequest request = new BorrowingCreateRequest();
        request.setMemberId(4L);

        assertThrows(MemberBlockedException.class, () -> service.create(request));
        verify(bookRepository, never()).findByIdForUpdate(any());
        verify(borrowingRepository, never()).save(any());
    }

    @Test
    void calculatesFineAndRestoresStockOnLateReturn() {
        Member member = org.mockito.Mockito.mock(Member.class);
        when(member.getId()).thenReturn(4L);
        when(member.getFullName()).thenReturn("Test Member");
        when(member.getEmail()).thenReturn("member@example.com");
        Book book = new Book("9780000000001", "Test book", "Author", 1, null);
        book.setAvailableQuantity(0);
        ReflectionTestUtils.setField(book, "id", 30L);
        Borrowing borrowing = new Borrowing(member, LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(1));
        BorrowingDetail detail = new BorrowingDetail(book, 1);
        borrowing.addDetail(detail);
        ReflectionTestUtils.setField(borrowing, "id", 10L);
        ReflectionTestUtils.setField(detail, "id", 20L);
        when(borrowingRepository.findById(10L)).thenReturn(Optional.of(borrowing));
        when(detailRepository.findById(20L)).thenReturn(Optional.of(detail));
        when(bookRepository.findByIdForUpdate(30L)).thenReturn(Optional.of(book));

        var returnLine = new com.example.library.dto.request.borrowing.BorrowingReturnRequest();
        returnLine.setDetailId(20L);
        returnLine.setQuantity(1);

        var response = service.returnBooks(10L, List.of(returnLine));

        assertEquals(Borrowing.RETURNED, response.status());
        assertEquals(new BigDecimal("5000"), response.totalFine());
        assertEquals(1, book.getAvailableQuantity());
        assertEquals(1, response.details().get(0).returnedQuantity());
    }
}
