package com.example.library.service.borrowing;

import com.example.library.dto.request.borrowing.BorrowingCreateRequest;
import com.example.library.dto.request.borrowing.BorrowingReturnRequest;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.BorrowingDetail;
import com.example.library.entity.Member;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.BorrowingNotFoundException;
import com.example.library.exception.InsufficientStockException;
import com.example.library.exception.MemberBlockedException;
import com.example.library.exception.MemberNotFoundException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.borrowing.BorrowingMapper;
import com.example.library.dto.response.borrowing.BorrowingResponse;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingDetailRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BorrowingService {
    private static final BigDecimal FINE_PER_DAY = BigDecimal.valueOf(5_000);

    private final BorrowingRepository borrowingRepository;
    private final BorrowingDetailRepository detailRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingMapper mapper;

    public BorrowingService(BorrowingRepository borrowingRepository,
                            BorrowingDetailRepository detailRepository,
                            BookRepository bookRepository, MemberRepository memberRepository,
                            BorrowingMapper mapper) {
        this.borrowingRepository = borrowingRepository;
        this.detailRepository = detailRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<BorrowingResponse> list(Pageable pageable) {
        return borrowingRepository.findAllByOrderByBorrowDateDescIdDesc(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BorrowingResponse get(Long id) { return mapper.toResponse(find(id)); }

    @Transactional
    public BorrowingResponse create(BorrowingCreateRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));
        if (!Member.ACTIVE.equals(member.getStatus())) throw new MemberBlockedException();
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            throw new com.example.library.exception.BusinessException("Phiếu mượn phải có ít nhất một cuốn sách.");
        }

        Map<Long, Long> requested = new TreeMap<>();
        request.getDetails().forEach(line -> {
            if (line == null || line.getBookId() == null || line.getQuantity() < 1) {
                throw new com.example.library.exception.BusinessException(
                        "Mỗi dòng mượn cần có sách và số lượng hợp lệ.");
            }
            requested.merge(line.getBookId(), (long) line.getQuantity(), Long::sum);
        });
        Borrowing borrowing = new Borrowing(member, LocalDate.now(), LocalDate.now().plusDays(14));
        for (Map.Entry<Long, Long> line : requested.entrySet()) {
            Book book = bookRepository.findByIdForUpdate(line.getKey())
                    .orElseThrow(() -> new BookNotFoundException(line.getKey()));
            long quantity = line.getValue();
            if (!Book.AVAILABLE.equals(book.getStatus()) || quantity < 1
                    || book.getAvailableQuantity() < quantity) {
                throw new InsufficientStockException(book.getTitle());
            }
            int checkedQuantity = Math.toIntExact(quantity);
            book.setAvailableQuantity(book.getAvailableQuantity() - checkedQuantity);
            borrowing.addDetail(new BorrowingDetail(book, checkedQuantity));
        }
        return mapper.toResponse(borrowingRepository.save(borrowing));
    }

    @Transactional
    public BorrowingResponse returnBooks(Long borrowingId, java.util.List<BorrowingReturnRequest> lines) {
        Borrowing borrowing = find(borrowingId);
        if (Borrowing.RETURNED.equals(borrowing.getStatus())) {
            throw new com.example.library.exception.BusinessException("Phiếu mượn này đã được trả đầy đủ.");
        }
        if (lines == null || lines.isEmpty()) {
            throw new com.example.library.exception.BusinessException("Hãy nhập số lượng cần trả.");
        }
        if (lines.stream().anyMatch(line -> line == null || line.getDetailId() == null || line.getQuantity() < 0)) {
            throw new com.example.library.exception.BusinessException("Thông tin trả sách không hợp lệ.");
        }
        if (lines.stream().noneMatch(line -> line.getQuantity() > 0)) {
            throw new com.example.library.exception.BusinessException("Hãy nhập số lượng cần trả.");
        }
        LocalDate today = LocalDate.now();
        for (BorrowingReturnRequest line : lines) {
            if (line.getQuantity() == 0) continue;
            BorrowingDetail detail = detailRepository.findById(line.getDetailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng sách mượn."));
            if (!detail.getBorrowing().getId().equals(borrowingId)) {
                throw new com.example.library.exception.BusinessException("Dòng sách không thuộc phiếu mượn này.");
            }
            if (line.getQuantity() < 0 || line.getQuantity() > detail.getOutstandingQuantity()) {
                throw new com.example.library.exception.BusinessException("Số lượng trả vượt quá số lượng đang mượn.");
            }
            Book book = bookRepository.findByIdForUpdate(detail.getBook().getId())
                    .orElseThrow(() -> new BookNotFoundException(detail.getBook().getId()));
            long overdueDays = Math.max(0, ChronoUnit.DAYS.between(borrowing.getDueDate(), today));
            BigDecimal fine = FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays))
                    .multiply(BigDecimal.valueOf(line.getQuantity()));
            detail.returnQuantity(line.getQuantity(), fine, today);
            book.setAvailableQuantity(book.getAvailableQuantity() + line.getQuantity());
            borrowing.setTotalFine(borrowing.getTotalFine().add(fine));
        }
        boolean allReturned = borrowing.getDetails().stream().allMatch(d -> d.getOutstandingQuantity() == 0);
        if (allReturned) {
            borrowing.setStatus(Borrowing.RETURNED);
            borrowing.setReturnedDate(today);
        } else if (borrowing.getDetails().stream().anyMatch(d -> d.getReturnedQuantity() > 0)) {
            borrowing.setStatus(Borrowing.PARTIALLY_RETURNED);
        }
        return mapper.toResponse(borrowing);
    }

    private Borrowing find(Long id) {
        return borrowingRepository.findById(id).orElseThrow(() -> new BorrowingNotFoundException(id));
    }
}
