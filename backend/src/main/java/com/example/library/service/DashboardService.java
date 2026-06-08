package com.example.library.service;

import com.example.library.dto.AuthDtos;
import com.example.library.dto.DashboardDto;
import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Loan;
import com.example.library.entity.Notification;
import com.example.library.entity.ReaderProfile;
import com.example.library.exception.ApiException;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.NotificationRepository;
import com.example.library.repository.ReaderProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final LibraryCardRepository cardRepository;
    private final ReaderProfileRepository profileRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;

    public DashboardService(
            LibraryCardRepository cardRepository,
            ReaderProfileRepository profileRepository,
            LoanRepository loanRepository,
            NotificationRepository notificationRepository,
            BookCopyRepository copyRepository,
            BookRepository bookRepository
    ) {
        this.cardRepository = cardRepository;
        this.profileRepository = profileRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(UUID userId) {
        LibraryCard card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Билет не найден", HttpStatus.NOT_FOUND));

        ReaderProfile profile = profileRepository.findById(userId).orElse(null);
        String fullName = profile != null ? profile.getFullName() : "Читатель";

        long active = loanRepository.countByCardIdAndStatus(card.getId(), "ACTIVE");
        long overdue = loanRepository.countByCardIdAndStatusAndDueDateBefore(
                card.getId(), "ACTIVE", LocalDate.now());

        List<DashboardDto.NotificationItem> notifications = buildNotifications(userId, card.getId());

        return new DashboardDto(
                new DashboardDto.CardSummary(card.getCardNumber(), card.getStatus()),
                new AuthDtos.UserBrief(userId.toString(), fullName, card.getCardNumber()),
                new DashboardDto.LoanCounts((int) active, (int) overdue),
                notifications
        );
    }

    private List<DashboardDto.NotificationItem> buildNotifications(UUID userId, UUID cardId) {
        List<DashboardDto.NotificationItem> result = new ArrayList<>();

        List<Loan> activeLoans = loanRepository.findByCardIdAndStatusOrderByLoanDateDesc(cardId, "ACTIVE");
        LocalDate today = LocalDate.now();
        for (Loan loan : activeLoans) {
            long daysLeft = ChronoUnit.DAYS.between(today, loan.getDueDate());
            String bookTitle = resolveBookTitle(loan);
            if (daysLeft < 0) {
                result.add(new DashboardDto.NotificationItem(
                        loan.getId().toString(),
                        "OVERDUE",
                        "Просрочено",
                        "Книга «" + bookTitle + "» — срок возврата истёк",
                        false
                ));
            } else if (daysLeft <= 14) {
                String body = daysLeft == 0
                        ? "Книга «" + bookTitle + "» — вернуть сегодня"
                        : "Книга «" + bookTitle + "» — вернуть через " + daysLeft + " "
                        + daysWord(daysLeft);
                result.add(new DashboardDto.NotificationItem(
                        loan.getId().toString(),
                        "RETURN_SOON",
                        "Скоро срок возврата",
                        body,
                        false
                ));
            }
        }

        notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(n -> !"RETURN_SOON".equals(n.getType()) && !"OVERDUE".equals(n.getType()))
                .map(this::toNotificationItem)
                .forEach(result::add);

        return result.stream().limit(5).toList();
    }

    private String resolveBookTitle(Loan loan) {
        BookCopy copy = copyRepository.findById(loan.getCopyId()).orElse(null);
        Book book = copy != null ? bookRepository.findById(copy.getBookId()).orElse(null) : null;
        return book != null ? book.getTitle() : "Книга";
    }

    private static String daysWord(long days) {
        long n = days % 100;
        if (n >= 11 && n <= 14) return "дней";
        return switch ((int) (days % 10)) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }

    private DashboardDto.NotificationItem toNotificationItem(Notification n) {
        return new DashboardDto.NotificationItem(
                n.getId().toString(),
                n.getType(),
                n.getTitle(),
                n.getBody(),
                n.isReadFlag()
        );
    }
}
