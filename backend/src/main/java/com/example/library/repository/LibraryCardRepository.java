package com.example.library.repository;

import com.example.library.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LibraryCardRepository extends JpaRepository<LibraryCard, UUID> {
    Optional<LibraryCard> findByCardNumber(String cardNumber);
    Optional<LibraryCard> findByUserId(UUID userId);
    boolean existsByCardNumber(String cardNumber);

    @Query(value = "SELECT COALESCE(MAX(CAST(card_number AS INT)), 10000) + 1 FROM library_card", nativeQuery = true)
    int nextCardNumberInt();
}
