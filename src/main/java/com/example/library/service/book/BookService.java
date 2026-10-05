package com.example.library.service.book;

import com.example.library.dto.request.book.BookCreateRequest;
import com.example.library.dto.request.book.BookUpdateRequest;
import com.example.library.dto.response.book.BookResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.exception.BookAlreadyExistsException;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.CategoryNotFoundException;
import com.example.library.mapper.book.BookMapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingDetailRepository;
import com.example.library.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BorrowingDetailRepository detailRepository;
    private final BookMapper mapper;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
                       BorrowingDetailRepository detailRepository, BookMapper mapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.detailRepository = detailRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> search(String query, Long categoryId, Pageable pageable) {
        return bookRepository.search(normalize(query), categoryId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookResponse get(Long id) { return mapper.toResponse(find(id)); }

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        String isbn = request.getIsbn().trim();
        if (bookRepository.existsByIsbnIgnoreCase(isbn)) throw new BookAlreadyExistsException(isbn);
        Category category = findActiveCategory(request.getCategoryId());
        return mapper.toResponse(bookRepository.save(
                new Book(isbn, request.getTitle().trim(), request.getAuthor().trim(),
                        request.getTotalQuantity(), category)));
    }

    @Transactional
    public BookResponse update(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        if (Book.DELETED.equals(book.getStatus())) throw new BookNotFoundException(id);
        String isbn = request.getIsbn().trim();
        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(isbn, id)) {
            throw new BookAlreadyExistsException(isbn);
        }
        Category category = findActiveCategory(request.getCategoryId());
        try {
            book.adjustInventory(request.getTotalQuantity());
        } catch (IllegalArgumentException exception) {
            throw new com.example.library.exception.BusinessException(exception.getMessage());
        }
        book.setIsbn(isbn);
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setCategory(category);
        return mapper.toResponse(book);
    }

    @Transactional
    public void deactivate(Long id) {
        Book book = find(id);
        if (detailRepository.hasOutstandingLoans(id)) {
            throw new com.example.library.exception.BusinessException("Không thể xóa sách đang có lượt mượn chưa trả.");
        }
        book.setStatus(Book.DELETED);
    }

    private Book find(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        if (Book.DELETED.equals(book.getStatus())) throw new BookNotFoundException(id);
        return book;
    }

    private Category findActiveCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        if (!category.isActive()) {
            throw new com.example.library.exception.BusinessException("Không thể chọn thể loại đã ngừng hoạt động.");
        }
        return category;
    }

    private static String normalize(String value) { return value == null ? "" : value.trim(); }
}
