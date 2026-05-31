package com.example.library.service;

import com.example.library.dto.AuthDtos;
import com.example.library.dto.DashboardDto;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Notification;
import com.example.library.entity.ReaderProfile;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.NotificationRepository;
import com.example.library.repository.ReaderProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final LibraryCardRepository cardRepository;
    private final ReaderProfileRepository profileRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;

    public DashboardService(
            LibraryCardRepository cardRepository,
            ReaderProfileRepository profileRepository,
            LoanRepository loanRepository,
            NotificationRepository notificationRepository
    ) {
        this.cardRepository = cardRepository;
        this.profileRepository = profileRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
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

        List<DashboardDto.NotificationItem> notifications = notificationRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toNotificationItem)
                .toList();

        return new DashboardDto(
                new DashboardDto.CardSummary(card.getCardNumber(), card.getStatus()),
                new AuthDtos.UserBrief(userId.toString(), fullName, card.getCardNumber()),
                new DashboardDto.LoanCounts((int) active, (int) overdue),
                notifications
        );
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
