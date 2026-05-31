package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "library_card")
public class LibraryCard {

    @Id
    private UUID id;

    @Column(name = "card_number", nullable = false, unique = true, length = 5)
    private String cardNumber;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String status;

    @Column(name = "holder_last_name")
    private String holderLastName;

    @Column(name = "holder_first_name")
    private String holderFirstName;

    @Column(name = "holder_birth_date")
    private LocalDate holderBirthDate;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (issuedAt == null) issuedAt = LocalDate.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHolderLastName() { return holderLastName; }
    public void setHolderLastName(String holderLastName) { this.holderLastName = holderLastName; }
    public String getHolderFirstName() { return holderFirstName; }
    public void setHolderFirstName(String holderFirstName) { this.holderFirstName = holderFirstName; }
    public LocalDate getHolderBirthDate() { return holderBirthDate; }
    public void setHolderBirthDate(LocalDate holderBirthDate) { this.holderBirthDate = holderBirthDate; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
