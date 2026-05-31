package com.example.library.service;

import com.example.library.dto.CardDtos;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.ReaderProfile;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.ReaderProfileRepository;
import com.example.library.security.QrSignatureService;
import com.example.library.util.QrTokenCodec;
import com.example.library.web.CardWebPaths;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
public class CardService {

    private static final long QR_TTL_SECONDS = 3600;

    private final LibraryCardRepository cardRepository;
    private final ReaderProfileRepository profileRepository;
    private final QrSignatureService qrSignatureService;
    private final QrTokenCodec qrTokenCodec;

    private static final DateTimeFormatter VALID_UNTIL_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm")
                    .withZone(ZoneId.systemDefault());

    public CardService(
            LibraryCardRepository cardRepository,
            ReaderProfileRepository profileRepository,
            QrSignatureService qrSignatureService,
            QrTokenCodec qrTokenCodec
    ) {
        this.cardRepository = cardRepository;
        this.profileRepository = profileRepository;
        this.qrSignatureService = qrSignatureService;
        this.qrTokenCodec = qrTokenCodec;
    }

    @Transactional(readOnly = true)
    public CardDtos.QrResponse getQrForUser(UUID userId, String publicBaseUrl) {
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
        String token = qrTokenCodec.encode(payload);
        String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
        String scanUrl = base + CardWebPaths.TICKET_PAGE + "?token=" + token;
        return new CardDtos.QrResponse(payload, display, scanUrl);
    }

    @Transactional(readOnly = true)
    public CardDtos.QrCardView resolveQrToken(String token) {
        CardDtos.QrPayload payload = qrTokenCodec.decode(token);
        long now = Instant.now().getEpochSecond();
        if (now > payload.exp()) {
            throw new IllegalArgumentException("Срок действия QR-кода истёк. Обновите код в приложении.");
        }

        UUID userId;
        try {
            userId = UUID.fromString(payload.uid());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный QR-код");
        }

        if (!qrSignatureService.verify(payload.v(), userId, payload.card(), payload.iat(), payload.exp(), payload.sig())) {
            throw new IllegalArgumentException("Подпись QR-кода недействительна");
        }

        LibraryCard card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден"));

        if (!card.getCardNumber().equals(payload.card())) {
            throw new IllegalArgumentException("Данные билета не совпадают");
        }

        ReaderProfile profile = profileRepository.findById(userId).orElse(null);
        String fullName = profile != null ? profile.getFullName() : "Читатель";
        String validUntil = VALID_UNTIL_FORMAT.format(Instant.ofEpochSecond(payload.exp()));

        return new CardDtos.QrCardView(fullName, card.getCardNumber(), card.getStatus(), validUntil);
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
