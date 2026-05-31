package com.example.library.service;

import com.example.library.dto.BookDtos;
import com.example.library.dto.LoanDtos;
import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Loan;
import com.example.library.exception.ApiException;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private static final int MAX_RENEWALS = 2;
    private static final int RENEW_DAYS = 14;

    private final LoanRepository loanRepository;
    private final LibraryCardRepository cardRepository;
    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;

    public LoanService(
            LoanRepository loanRepository,
            LibraryCardRepository cardRepository,
            BookCopyRepository copyRepository,
            BookRepository bookRepository
    ) {
        this.loanRepository = loanRepository;
        this.cardRepository = cardRepository;
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<LoanDtos.LoanItem> getActiveLoans(UUID userId) {
        LibraryCard card = requireCard(userId);
        return loanRepository.findByCardIdAndStatusOrderByLoanDateDesc(card.getId(), "ACTIVE")
                .stream()
                .map(this::toLoanItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanDtos.LoanItem> getHistory(UUID userId) {
        LibraryCard card = requireCard(userId);
        return loanRepository.findByCardIdAndStatusOrderByReturnedAtDesc(card.getId(), "RETURNED")
                .stream()
                .map(this::toLoanItem)
                .toList();
    }

    @Transactional
    public BookDtos.RenewResponse renewLoan(UUID userId, UUID loanId) {
        LibraryCard card = requireCard(userId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Выдача не найдена", HttpStatus.NOT_FOUND));

        if (!loan.getCardId().equals(card.getId())) {
            throw new ApiException("FORBIDDEN", "Нет доступа к этой выдаче", HttpStatus.FORBIDDEN);
        }
        if (!"ACTIVE".equals(loan.getStatus())) {
            throw new ApiException("INVALID_STATUS", "Книга уже возвращена", HttpStatus.BAD_REQUEST);
        }
        if (loan.getRenewalCount() >= MAX_RENEWALS) {
            throw new ApiException("RENEWAL_LIMIT", "Лимит продлений исчерпан", HttpStatus.BAD_REQUEST);
        }

        loan.setDueDate(loan.getDueDate().plusDays(RENEW_DAYS));
        loan.setRenewalCount(loan.getRenewalCount() + 1);
        loanRepository.save(loan);

        return new BookDtos.RenewResponse(
                loan.getId().toString(),
                loan.getDueDate().toString(),
                loan.getRenewalCount()
        );
    }

    private LibraryCard requireCard(UUID userId) {
        return cardRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Билет не найден", HttpStatus.NOT_FOUND));
    }

    private LoanDtos.LoanItem toLoanItem(Loan loan) {
        BookCopy copy = copyRepository.findById(loan.getCopyId()).orElse(null);
        Book book = copy != null ? bookRepository.findById(copy.getBookId()).orElse(null) : null;
        String title = book != null ? book.getTitle() : "Книга";
        String author = book != null ? book.getAuthorName() : "";
        boolean overdue = "ACTIVE".equals(loan.getStatus())
                && loan.getDueDate().isBefore(LocalDate.now());
        boolean canRenew = "ACTIVE".equals(loan.getStatus()) && loan.getRenewalCount() < MAX_RENEWALS;

        return new LoanDtos.LoanItem(
                loan.getId().toString(),
                title,
                author,
                loan.getLoanDate().toString(),
                loan.getDueDate().toString(),
                loan.getReturnedAt() != null ? loan.getReturnedAt().toString() : null,
                loan.getStatus(),
                loan.getRenewalCount(),
                canRenew,
                overdue
        );
    }
}
