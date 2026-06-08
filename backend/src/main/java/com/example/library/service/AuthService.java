package com.example.library.service;

import com.example.library.dto.AuthDtos;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.ReaderProfile;
import com.example.library.entity.UserAccount;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.ReaderProfileRepository;
import com.example.library.repository.UserAccountRepository;
import com.example.library.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final ReaderProfileRepository profileRepository;
    private final LibraryCardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userRepository,
            ReaderProfileRepository profileRepository,
            LibraryCardRepository cardRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        String login = request.login().trim();
        LibraryCard card;
        UserAccount user;

        if (login.contains("@")) {
            user = userRepository.findByEmail(login)
                    .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Пользователь не найден", HttpStatus.NOT_FOUND));
            card = cardRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Читательский билет не найден", HttpStatus.NOT_FOUND));
        } else if (login.matches("\\d{5}")) {
            card = cardRepository.findByCardNumber(login)
                    .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Читательский билет не найден", HttpStatus.NOT_FOUND));
            if (card.getUserId() == null) {
                throw new ApiException("CARD_NOT_LINKED", "Билет не привязан к аккаунту", HttpStatus.BAD_REQUEST);
            }
            user = userRepository.findById(card.getUserId())
                    .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Пользователь не найден", HttpStatus.NOT_FOUND));
        } else {
            String phone = normalizePhone(login);
            user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Пользователь не найден", HttpStatus.NOT_FOUND));
            card = cardRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Читательский билет не найден", HttpStatus.NOT_FOUND));
        }

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new ApiException("CARD_BLOCKED", "Билет заблокирован или недоступен", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", "Неверный пароль", HttpStatus.UNAUTHORIZED);
        }

        ReaderProfile profile = profileRepository.findById(user.getId()).orElse(null);
        String fullName = profile != null ? profile.getFullName() : "Читатель";
        String token = jwtService.createToken(user.getId(), card.getCardNumber());

        return new AuthDtos.LoginResponse(token, 24 * 3600L,
                new AuthDtos.UserBrief(user.getId().toString(), fullName, card.getCardNumber()));
    }

    private static String normalizePhone(String raw) {
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() == 11 && digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }
        if (digits.length() == 10 && digits.startsWith("9")) {
            digits = "7" + digits;
        }
        if (digits.startsWith("7")) {
            return "+" + digits;
        }
        return raw.trim();
    }
}
