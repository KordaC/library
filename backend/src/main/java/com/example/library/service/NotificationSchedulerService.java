package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.FcmToken;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Loan;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookGenreRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.FcmTokenRepository;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.LoanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationSchedulerService {

    private static final int NEW_ARRIVAL_DAYS = 7;
    private static final int RETURN_REMINDER_DAYS = 14;

    private final FcmTokenRepository fcmTokenRepository;
    private final LoanRepository loanRepository;
    private final LibraryCardRepository cardRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;
    private final BookGenreRepository bookGenreRepository;
    private final NotificationDispatchService dispatchService;

    public NotificationSchedulerService(
            FcmTokenRepository fcmTokenRepository,
            LoanRepository loanRepository,
            LibraryCardRepository cardRepository,
            BookRepository bookRepository,
            BookCopyRepository copyRepository,
            BookGenreRepository bookGenreRepository,
            NotificationDispatchService dispatchService
    ) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.loanRepository = loanRepository;
        this.cardRepository = cardRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.dispatchService = dispatchService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendDailyNotifications() {
        sendReturnReminders();
        sendRecommendations();
        sendNewArrivals();
    }

    private void sendReturnReminders() {
        LocalDate today = LocalDate.now();
        for (Loan loan : loanRepository.findAllActive()) {
            LibraryCard card = cardRepository.findById(loan.getCardId()).orElse(null);
            if (card == null || card.getUserId() == null) {
                continue;
            }
            long daysLeft = ChronoUnit.DAYS.between(today, loan.getDueDate());
            if (daysLeft > RETURN_REMINDER_DAYS) {
                continue;
            }
            String bookTitle = resolveBookTitle(loan);
            String type;
            String title;
            String body;
            if (daysLeft < 0) {
                type = "OVERDUE";
                title = "Просрочено";
                body = "Книга «" + bookTitle + "» — срок возврата истёк";
            } else {
                type = "RETURN_SOON";
                title = "Скоро срок возврата";
                body = daysLeft == 0
                        ? "Книга «" + bookTitle + "» — вернуть сегодня"
                        : "Книга «" + bookTitle + "» — вернуть через " + daysLeft + " " + daysWord(daysLeft);
            }
            String dedupKey = type + ":" + loan.getId() + ":" + loan.getDueDate();
            dispatchService.dispatch(
                    card.getUserId(),
                    type,
                    title,
                    body,
                    dedupKey,
                    Map.of("type", type, "loanId", loan.getId().toString())
            );
        }
    }

    private void sendRecommendations() {
        Set<UUID> userIds = fcmTokenRepository.findAll().stream()
                .map(FcmToken::getUserId)
                .collect(Collectors.toSet());
        for (UUID userId : userIds) {
            LibraryCard card = cardRepository.findByUserId(userId).orElse(null);
            if (card == null) {
                continue;
            }
            Book recommendation = findRecommendation(card.getId());
            if (recommendation == null) {
                continue;
            }
            String author = recommendation.getAuthorName() != null
                    ? recommendation.getAuthorName()
                    : "";
            String body = author.isEmpty()
                    ? "Попробуйте «" + recommendation.getTitle() + "»"
                    : "Попробуйте «" + recommendation.getTitle() + "» — " + author;
            String weekKey = LocalDate.now().with(java.time.DayOfWeek.MONDAY).toString();
            dispatchService.dispatch(
                    userId,
                    "RECOMMENDATION",
                    "Рекомендация для вас",
                    body,
                    "RECOMMENDATION:" + userId + ":" + weekKey + ":" + recommendation.getId(),
                    Map.of("type", "RECOMMENDATION", "bookId", recommendation.getId().toString())
            );
        }
    }

    private void sendNewArrivals() {
        LocalDateTime since = LocalDateTime.now().minusDays(NEW_ARRIVAL_DAYS);
        List<Book> newBooks = bookRepository.findCreatedSince(since);
        if (newBooks.isEmpty()) {
            return;
        }
        Set<UUID> userIds = fcmTokenRepository.findAll().stream()
                .map(FcmToken::getUserId)
                .collect(Collectors.toSet());
        for (UUID userId : userIds) {
            LibraryCard card = cardRepository.findByUserId(userId).orElse(null);
            if (card == null) {
                continue;
            }
            Set<UUID> preferredGenres = collectPreferredGenres(card.getId());
            for (Book book : newBooks) {
                if (!matchesUserGenres(book.getId(), preferredGenres)) {
                    continue;
                }
                if (copyRepository.countByBookIdAndStatus(book.getId(), "AVAILABLE") == 0) {
                    continue;
                }
                dispatchService.dispatch(
                        userId,
                        "NEW_ARRIVAL",
                        "Новое поступление",
                        "В каталоге появилась «" + book.getTitle() + "»",
                        "NEW_ARRIVAL:" + book.getId(),
                        Map.of("type", "NEW_ARRIVAL", "bookId", book.getId().toString())
                );
            }
        }
    }

    private Book findRecommendation(UUID cardId) {
        Set<UUID> borrowedBookIds = new HashSet<>();
        Set<UUID> genreIds = new HashSet<>();
        for (Loan loan : loanRepository.findByCardId(cardId)) {
            BookCopy copy = copyRepository.findById(loan.getCopyId()).orElse(null);
            if (copy == null) {
                continue;
            }
            borrowedBookIds.add(copy.getBookId());
            genreIds.addAll(bookGenreRepository.findGenreIdsByBookId(copy.getBookId()));
        }
        if (genreIds.isEmpty()) {
            return null;
        }
        List<UUID> candidateIds = bookGenreRepository.findBookIdsByGenreIds(new ArrayList<>(genreIds));
        for (UUID bookId : candidateIds) {
            if (borrowedBookIds.contains(bookId)) {
                continue;
            }
            if (copyRepository.countByBookIdAndStatus(bookId, "AVAILABLE") == 0) {
                continue;
            }
            return bookRepository.findById(bookId).orElse(null);
        }
        return null;
    }

    private Set<UUID> collectPreferredGenres(UUID cardId) {
        Set<UUID> genreIds = new HashSet<>();
        for (Loan loan : loanRepository.findByCardId(cardId)) {
            BookCopy copy = copyRepository.findById(loan.getCopyId()).orElse(null);
            if (copy == null) {
                continue;
            }
            genreIds.addAll(bookGenreRepository.findGenreIdsByBookId(copy.getBookId()));
        }
        return genreIds;
    }

    private boolean matchesUserGenres(UUID bookId, Set<UUID> preferredGenres) {
        if (preferredGenres.isEmpty()) {
            return true;
        }
        return bookGenreRepository.findGenreIdsByBookId(bookId).stream()
                .anyMatch(preferredGenres::contains);
    }

    private String resolveBookTitle(Loan loan) {
        BookCopy copy = copyRepository.findById(loan.getCopyId()).orElse(null);
        Book book = copy != null ? bookRepository.findById(copy.getBookId()).orElse(null) : null;
        return book != null ? book.getTitle() : "Книга";
    }

    private static String daysWord(long days) {
        long n = days % 100;
        if (n >= 11 && n <= 14) {
            return "дней";
        }
        return switch ((int) (days % 10)) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }
}
