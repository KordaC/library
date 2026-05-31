package com.example.library.service;

import com.example.library.dto.AuthDtos;
import com.example.library.dto.RegistrationDtos;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.ReaderProfile;
import com.example.library.entity.RegistrationRequest;
import com.example.library.entity.UserAccount;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.ReaderProfileRepository;
import com.example.library.repository.RegistrationRequestRepository;
import com.example.library.repository.UserAccountRepository;
import com.example.library.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private final LibraryCardRepository cardRepository;
    private final UserAccountRepository userRepository;
    private final ReaderProfileRepository profileRepository;
    private final RegistrationRequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegistrationService(
            LibraryCardRepository cardRepository,
            UserAccountRepository userRepository,
            ReaderProfileRepository profileRepository,
            RegistrationRequestRepository requestRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public RegistrationDtos.VerifyCardResponse verifyCard(RegistrationDtos.VerifyCardRequest request) {
        LibraryCard card = cardRepository.findByCardNumber(request.cardNumber())
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Читательский билет не найден", HttpStatus.NOT_FOUND));
        if (card.getUserId() != null) {
            throw new ApiException("CARD_ALREADY_LINKED", "Билет уже привязан к аккаунту", HttpStatus.CONFLICT);
        }
        if (!"UNASSIGNED".equals(card.getStatus())) {
            throw new ApiException("CARD_UNAVAILABLE", "Билет недоступен для привязки", HttpStatus.BAD_REQUEST);
        }
        return new RegistrationDtos.VerifyCardResponse(true, "birthDate");
    }

    @Transactional
    public AuthDtos.LoginResponse linkCard(RegistrationDtos.LinkCardRequest request) {
        validatePasswords(request.password(), request.passwordConfirm());

        LibraryCard card = cardRepository.findByCardNumber(request.cardNumber())
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Читательский билет не найден", HttpStatus.NOT_FOUND));

        if (card.getUserId() != null) {
            throw new ApiException("CARD_ALREADY_LINKED", "Билет уже привязан", HttpStatus.CONFLICT);
        }
        if (card.getHolderBirthDate() == null || !card.getHolderBirthDate().equals(request.birthDate())) {
            throw new ApiException("VERIFICATION_FAILED", "Дата рождения не совпадает", HttpStatus.BAD_REQUEST);
        }

        UserAccount user = createUserFromCard(card, request.password());
        linkCardToUser(card, user.getId());

        ReaderProfile profile = profileRepository.findById(user.getId()).orElseThrow();
        String token = jwtService.createToken(user.getId(), card.getCardNumber());
        return new AuthDtos.LoginResponse(token, 24 * 3600L,
                new AuthDtos.UserBrief(user.getId().toString(), profile.getFullName(), card.getCardNumber()));
    }

    @Transactional
    public RegistrationDtos.NewRegistrationResponse createRequest(RegistrationDtos.NewRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException("EMAIL_EXISTS", "Email уже используется", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new ApiException("PHONE_EXISTS", "Телефон уже используется", HttpStatus.CONFLICT);
        }

        RegistrationRequest entity = new RegistrationRequest();
        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setMiddleName(request.middleName());
        entity.setBirthDate(request.birthDate());
        entity.setPassportSeries(request.passportSeries());
        entity.setPassportNumber(request.passportNumber());
        entity.setAddress(request.address());
        entity.setPhone(request.phone());
        entity.setEmail(request.email());
        entity.setStatus("PENDING_PAYMENT");
        requestRepository.save(entity);

        return new RegistrationDtos.NewRegistrationResponse(entity.getId().toString(), entity.getStatus());
    }

    @Transactional
    public RegistrationDtos.MockPayResponse mockPay(UUID requestId) {
        RegistrationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Заявка не найдена", HttpStatus.NOT_FOUND));

        if ("PAID_WAITING_PASSWORD".equals(req.getStatus()) && req.getCreatedCardId() != null) {
            LibraryCard existing = cardRepository.findById(req.getCreatedCardId()).orElseThrow();
            return new RegistrationDtos.MockPayResponse("SUCCESS", existing.getCardNumber(),
                    "Оплата уже подтверждена");
        }
        if (!"PENDING_PAYMENT".equals(req.getStatus())) {
            throw new ApiException("INVALID_STATUS", "Заявка в неверном статусе", HttpStatus.BAD_REQUEST);
        }

        String cardNumber = generateUniqueCardNumber();
        LibraryCard card = new LibraryCard();
        card.setCardNumber(cardNumber);
        card.setStatus("UNASSIGNED");
        card.setHolderLastName(req.getLastName());
        card.setHolderFirstName(req.getFirstName());
        card.setHolderBirthDate(req.getBirthDate());
        cardRepository.save(card);

        req.setStatus("PAID_WAITING_PASSWORD");
        req.setCreatedCardId(card.getId());
        requestRepository.save(req);

        return new RegistrationDtos.MockPayResponse("SUCCESS", cardNumber,
                "Оплата учебным способом подтверждена");
    }

    @Transactional
    public AuthDtos.LoginResponse completeRegistration(UUID requestId, RegistrationDtos.CompleteRegistrationRequest body) {
        validatePasswords(body.password(), body.passwordConfirm());

        RegistrationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Заявка не найдена", HttpStatus.NOT_FOUND));

        if (!"PAID_WAITING_PASSWORD".equals(req.getStatus()) || req.getCreatedCardId() == null) {
            throw new ApiException("INVALID_STATUS", "Сначала подтвердите оплату", HttpStatus.BAD_REQUEST);
        }

        LibraryCard card = cardRepository.findById(req.getCreatedCardId()).orElseThrow();
        if (card.getUserId() != null) {
            throw new ApiException("ALREADY_COMPLETED", "Регистрация уже завершена", HttpStatus.CONFLICT);
        }

        UserAccount user = new UserAccount();
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(body.password()));
        user.setStatus("ACTIVE");
        userRepository.save(user);

        ReaderProfile profile = new ReaderProfile();
        profile.setUser(user);
        profile.setLastName(req.getLastName());
        profile.setFirstName(req.getFirstName());
        profile.setMiddleName(req.getMiddleName());
        profile.setBirthDate(req.getBirthDate());
        profile.setPassportSeries(req.getPassportSeries());
        profile.setPassportNumber(req.getPassportNumber());
        profile.setAddress(req.getAddress());
        profileRepository.save(profile);

        linkCardToUser(card, user.getId());
        req.setStatus("COMPLETED");
        requestRepository.save(req);

        String token = jwtService.createToken(user.getId(), card.getCardNumber());
        return new AuthDtos.LoginResponse(token, 24 * 3600L,
                new AuthDtos.UserBrief(user.getId().toString(), profile.getFullName(), card.getCardNumber()));
    }

    @Transactional(readOnly = true)
    public RegistrationDtos.RegistrationStatusResponse getStatus(UUID requestId) {
        RegistrationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Заявка не найдена", HttpStatus.NOT_FOUND));
        String cardNumber = null;
        if (req.getCreatedCardId() != null) {
            cardNumber = cardRepository.findById(req.getCreatedCardId()).map(LibraryCard::getCardNumber).orElse(null);
        }
        return new RegistrationDtos.RegistrationStatusResponse(req.getStatus(), cardNumber);
    }

    private UserAccount createUserFromCard(LibraryCard card, String password) {
        UserAccount user = new UserAccount();
        user.setEmail(card.getCardNumber() + "@linked.library.local");
        user.setPhone(null);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus("ACTIVE");
        userRepository.save(user);

        ReaderProfile profile = new ReaderProfile();
        profile.setUser(user);
        profile.setLastName(card.getHolderLastName());
        profile.setFirstName(card.getHolderFirstName());
        profile.setBirthDate(card.getHolderBirthDate());
        profileRepository.save(profile);

        return user;
    }

    private void linkCardToUser(LibraryCard card, UUID userId) {
        card.setUserId(userId);
        card.setStatus("ACTIVE");
        card.setRegisteredAt(LocalDateTime.now());
        cardRepository.save(card);
    }

    private String generateUniqueCardNumber() {
        for (int i = 0; i < 50; i++) {
            int num = cardRepository.nextCardNumberInt();
            if (num > 99999) num = 10000 + i;
            String candidate = String.format("%05d", num);
            if (!cardRepository.existsByCardNumber(candidate)) {
                return candidate;
            }
        }
        throw new ApiException("CARD_POOL_EXHAUSTED", "Не удалось выдать номер билета", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void validatePasswords(String password, String confirm) {
        if (!password.equals(confirm)) {
            throw new ApiException("PASSWORD_MISMATCH", "Пароли не совпадают", HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 8 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new ApiException("WEAK_PASSWORD", "Пароль: мин. 8 символов, буква и цифра", HttpStatus.BAD_REQUEST);
        }
    }
}
