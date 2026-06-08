package com.example.library.service;

import com.example.library.dto.BookDtos;
import com.example.library.entity.Book;
import com.example.library.exception.ApiException;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookGenreRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.GenreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final BookCopyRepository copyRepository;
    private final BookGenreRepository bookGenreRepository;

    public CatalogService(
            BookRepository bookRepository,
            GenreRepository genreRepository,
            BookCopyRepository copyRepository,
            BookGenreRepository bookGenreRepository
    ) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.copyRepository = copyRepository;
        this.bookGenreRepository = bookGenreRepository;
    }

    @Transactional(readOnly = true)
    public List<BookDtos.GenreItem> listGenres() {
        return genreRepository.findAll().stream()
                .map(g -> new BookDtos.GenreItem(g.getId().toString(), g.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookDtos.BookListItem> listBooks(String q, UUID genreId, String sort) {
        String query = q != null ? q.trim() : "";
        List<Book> books = bookRepository.search(query.isEmpty() ? null : query, genreId);
        return books.stream()
                .map(this::toListItem)
                .sorted((a, b) -> compareSort(a, b, sort))
                .toList();
    }

    @Transactional(readOnly = true)
    public BookDtos.BookDetail getBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Книга не найдена", HttpStatus.NOT_FOUND));
        long available = copyRepository.countByBookIdAndStatus(bookId, "AVAILABLE");
        long total = copyRepository.countByBookId(bookId);
        List<String> genres = bookGenreRepository.findGenreNamesByBookId(bookId);
        return new BookDtos.BookDetail(
                book.getId().toString(),
                book.getTitle(),
                book.getAuthorName(),
                book.getDescription(),
                book.getPublicationYear(),
                book.getIsbn(),
                (int) available,
                (int) total,
                genres,
                resolveCoverUrl(book)
        );
    }

    @Transactional
    public BookDtos.BookDetail updateCover(UUID bookId, String coverImageUrl) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Книга не найдена", HttpStatus.NOT_FOUND));
        book.setCoverImageUrl(coverImageUrl != null ? coverImageUrl.trim() : null);
        bookRepository.save(book);
        return getBook(bookId);
    }

    private BookDtos.BookListItem toListItem(Book book) {
        int available = (int) copyRepository.countByBookIdAndStatus(book.getId(), "AVAILABLE");
        return new BookDtos.BookListItem(
                book.getId().toString(),
                book.getTitle(),
                book.getAuthorName(),
                available,
                book.getPublicationYear(),
                book.getIsbn(),
                resolveCoverUrl(book)
        );
    }

    private static String resolveCoverUrl(Book book) {
        if (book.getCoverImageUrl() != null && !book.getCoverImageUrl().isBlank()) {
            return book.getCoverImageUrl();
        }
        return coverImageUrlFromIsbn(book.getIsbn());
    }

    private static String coverImageUrlFromIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        String digits = isbn.replaceAll("[^0-9Xx]", "");
        if (digits.length() < 10) {
            return null;
        }
        return "https://covers.openlibrary.org/b/isbn/" + digits + "-L.jpg";
    }

    private int compareSort(BookDtos.BookListItem a, BookDtos.BookListItem b, String sort) {
        if ("author".equalsIgnoreCase(sort)) {
            String aa = a.authorName() != null ? a.authorName() : "";
            String ab = b.authorName() != null ? b.authorName() : "";
            return aa.compareToIgnoreCase(ab);
        }
        return a.title().compareToIgnoreCase(b.title());
    }
}
