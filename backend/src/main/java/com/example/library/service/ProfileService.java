package com.example.library.service;

import com.example.library.dto.ProfileDtos;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.ReaderProfile;
import com.example.library.entity.UserAccount;
import com.example.library.exception.ApiException;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.ReaderProfileRepository;
import com.example.library.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserAccountRepository userRepository;
    private final ReaderProfileRepository profileRepository;
    private final LibraryCardRepository cardRepository;

    public ProfileService(
            UserAccountRepository userRepository,
            ReaderProfileRepository profileRepository,
            LibraryCardRepository cardRepository
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public ProfileDtos.ProfileResponse getProfile(UUID userId) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Пользователь не найден", HttpStatus.NOT_FOUND));
        ReaderProfile profile = profileRepository.findById(userId).orElse(null);
        LibraryCard card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("CARD_NOT_FOUND", "Билет не найден", HttpStatus.NOT_FOUND));

        String fullName = profile != null ? profile.getFullName() : "Читатель";
        String birthDate = profile != null ? profile.getBirthDate().toString() : "";
        String address = profile != null && profile.getAddress() != null ? profile.getAddress() : "";

        return new ProfileDtos.ProfileResponse(
                userId.toString(),
                fullName,
                card.getCardNumber(),
                card.getStatus(),
                user.getEmail(),
                user.getPhone(),
                birthDate,
                address
        );
    }

    @Transactional
    public ProfileDtos.ProfileResponse updateProfile(UUID userId, ProfileDtos.UpdateProfileRequest request) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Пользователь не найден", HttpStatus.NOT_FOUND));

        if (request.email() != null && !request.email().isBlank()) {
            userRepository.findByEmail(request.email())
                    .filter(other -> !other.getId().equals(userId))
                    .ifPresent(x -> {
                        throw new ApiException("EMAIL_TAKEN", "Email уже используется", HttpStatus.CONFLICT);
                    });
            user.setEmail(request.email().trim());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            userRepository.findByPhone(request.phone())
                    .filter(other -> !other.getId().equals(userId))
                    .ifPresent(x -> {
                        throw new ApiException("PHONE_TAKEN", "Телефон уже используется", HttpStatus.CONFLICT);
                    });
            user.setPhone(request.phone().trim());
        }
        userRepository.save(user);

        ReaderProfile profile = profileRepository.findById(userId).orElse(null);
        if (profile != null) {
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);
        }

        return getProfile(userId);
    }
}
