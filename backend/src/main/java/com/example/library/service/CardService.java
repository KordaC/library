package com.example.library.service;

import com.example.library.dto.CardDtos;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.ReaderProfile;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.ReaderProfileRepository;
import com.example.library.security.QrSignatureService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class CardService {

    private static final long QR_TTL_SECONDS = 3600;

    private final LibraryCardRepository cardRepository;
    private final ReaderProfileRepository profileRepository;
    private final QrSignatureService qrSignatureService;

    public CardService(
            LibraryCardRepository cardRepository,
            ReaderProfileRepository profileRepository,
            QrSignatureService qrSignatureService
    ) {
        this.cardRepository = cardRepository;
        this.profileRepository = profileRepository;
        this.qrSignatureService = qrSignatureService;
    }

    @Transactional(readOnly = true)
    public CardDtos.QrResponse getQrForUser(UUID userId) {
        LibraryCard card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Билет не найден", HttpStatus.NOT_FOUND));

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new ApiException("CARD_BLOCKED", "Билет не активен", HttpStatus.FORBIDDEN);
        }

        long iat = Instant.now().getEpochSecond();
        long exp = iat + QR_TTL_SECONDS;
        String sig = qrSignatureService.sign(1, userId, card.getCardNumber(), iat, exp);

        CardDtos.QrPayload payload = new CardDtos.QrPayload(1, userId.toString(), card.getCardNumber(), iat, exp, sig);
        ReaderProfile profile = profileRepository.findById(userId).orElse(null);
        String fullName = profile != null ? profile.getFullName() : "Читатель";

        Map<String, String> display = Map.of(
                "fullName", fullName,
                "cardNumber", card.getCardNumber(),
                "status", card.getStatus()
        );
        return new CardDtos.QrResponse(payload, display);
    }

    @Transactional(readOnly = true)
    public CardDtos.CardResponse getMyCard(UUID userId) {
        LibraryCard card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Билет не найден", HttpStatus.NOT_FOUND));
        return new CardDtos.CardResponse(
                card.getCardNumber(),
                card.getStatus(),
                card.getIssuedAt().toString(),
                card.getRegisteredAt() != null ? card.getRegisteredAt().toLocalDate().toString() : null
        );
    }
}
